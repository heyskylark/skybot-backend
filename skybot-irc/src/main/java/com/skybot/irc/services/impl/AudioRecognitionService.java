package com.skybot.irc.services.impl;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechSettings;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.skybot.irc.services.IAudioRecognitionService;
import com.skybot.irc.services.IVoiceCommandService;
import com.skybot.irc.utility.TextColor;
import lombok.extern.slf4j.Slf4j;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.TargetDataLine;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class AudioRecognitionService implements IAudioRecognitionService {

    private static final int STREAMING_LIMIT = 10000; // ~10 seconds

    // Creating shared object
    private static volatile BlockingQueue<byte[]> sharedQueue = new LinkedBlockingQueue<>();
    private static TargetDataLine targetDataLine;
    private static int BYTES_PER_BUFFER = 6400; // buffer size in bytes

    private static int restartCounter = 0;
    private static int resultEndTimeInMS = 0;
    private static int isFinalEndTime = 0;
    private static double bridgingOffset = 0;
    private boolean lastTranscriptWasFinal = false;
    private static StreamController referenceToStreamController;

    private final IVoiceCommandService voiceCommandService;
    private final SpeechSettings speechSettings;
    private final TaskExecutor executor;

    @Autowired
    public AudioRecognitionService(IVoiceCommandService voiceCommandService,
                                   SpeechSettings speechSettings,
                                   TaskExecutor executor) {
        this.voiceCommandService = voiceCommandService;
        this.speechSettings = speechSettings;
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void streamingRecognize() throws Exception {
        lastTranscriptWasFinal = false;

        // Microphone Input buffering
        class MicBuffer implements Runnable {

            @Override
            public void run() {
                log.info(TextColor.YELLOW);
                log.info("Mic is now listening.");

                targetDataLine.start();
                byte[] data = new byte[BYTES_PER_BUFFER];
                while (targetDataLine.isOpen()) {
                    try {
                        int numBytesRead = targetDataLine.read(data, 0, data.length);
                        if ((numBytesRead <= 0) && (targetDataLine.isOpen())) {
                            continue;
                        }
                        sharedQueue.put(data.clone());
                    } catch (InterruptedException e) {
                        log.error("Microphone input buffering interrupted : {}", e.getMessage());
                    }
                }
            }
        }

        ResponseObserver<StreamingRecognizeResponse> responseObserver;
        try (SpeechClient client = SpeechClient.create(speechSettings)) {
            ClientStream<StreamingRecognizeRequest> clientStream;
            responseObserver =
                    new ResponseObserver<StreamingRecognizeResponse>() {

                        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                        public void onStart(StreamController controller) {
                            referenceToStreamController = controller;
                        }

                        public void onResponse(StreamingRecognizeResponse response) {
                            responses.add(response);

                            StreamingRecognitionResult result = response.getResultsList().get(0);

                            Duration resultEndTime = result.getResultEndTime();

                            resultEndTimeInMS = (int) ((resultEndTime.getSeconds() * 1000)
                                    + (resultEndTime.getNanos() / 1000000));

                            double correctedTime = resultEndTimeInMS - bridgingOffset
                                    + (STREAMING_LIMIT * restartCounter);
                            DecimalFormat format = new DecimalFormat("0.#");

                            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                            if (result.getIsFinal()) {
                                System.out.print(TextColor.GREEN);
                                System.out.print("\033[2K\r");
                                System.out.printf("%s: %s\n", format.format(correctedTime),
                                        alternative.getTranscript());

                                isFinalEndTime = resultEndTimeInMS;
                                lastTranscriptWasFinal = true;
                                System.out.print(TextColor.RESET);

                                voiceCommandService.findCommand(alternative.getTranscript());
                            } else {
                                System.out.print(TextColor.RED);
                                System.out.print("\033[2K\r");
                                System.out.printf("%s: %s", format.format(correctedTime),
                                        alternative.getTranscript());

                                lastTranscriptWasFinal = false;
                            }
                        }

                        public void onComplete() {
                        }

                        public void onError(Throwable t) {
                            log.error("Speech to text error: {}", t.getMessage());
                            t.printStackTrace();
                            //TODO crashes! Need to handle so it doesn't take down snowboy with it :(
                        }

                    };

            clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(16000)
                            .build();

            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder()
                            .setConfig(recognitionConfig)
                            .setInterimResults(true)
                            .build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);

            try {
                // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
                // bigEndian: false
                AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
                // Set the system information to read from the microphone audio stream
                Info targetInfo = new Info(TargetDataLine.class, audioFormat);

                if (!AudioSystem.isLineSupported(targetInfo)) {
                    System.out.println("Microphone not supported");
                    System.exit(0);
                }
                // Target data line captures the audio stream the microphone produces.
                targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
                targetDataLine.open(audioFormat);
                executor.execute(new MicBuffer());

                long startTime = System.currentTimeMillis();

                while (true) {

                    long estimatedTime = System.currentTimeMillis() - startTime;

                    if (lastTranscriptWasFinal || estimatedTime >= STREAMING_LIMIT) {
                        targetDataLine.close();
                        clientStream.closeSend();
                        referenceToStreamController.cancel(); // remove Observer

                        resultEndTimeInMS = 0;
                        break;
                    } else {
                        ByteString tempByteString = ByteString.copyFrom(sharedQueue.take());

                        request =
                                StreamingRecognizeRequest.newBuilder()
                                        .setAudioContent(tempByteString)
                                        .build();
                    }

                    clientStream.send(request);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
