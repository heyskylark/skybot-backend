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

    private static final String RED = "\033[0;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";

    // Creating shared object
    private static volatile BlockingQueue<byte[]> sharedQueue = new LinkedBlockingQueue<>();
    private static TargetDataLine targetDataLine;
    private static int BYTES_PER_BUFFER = 6400; // buffer size in bytes

    private static int restartCounter = 0;
    private static ArrayList<ByteString> audioInput  = new ArrayList<ByteString>();
    private static ArrayList<ByteString> lastAudioInput = new ArrayList<ByteString>();
    private static int resultEndTimeInMS = 0;
    private static int isFinalEndTime = 0;
    private static int finalRequestEndTime = 0;
    private static boolean newStream = true;
    private static double bridgingOffset = 0;
    private boolean lastTranscriptWasFinal = false;
    private static StreamController referenceToStreamController;
    private static ByteString tempByteString;

    private final SpeechSettings speechSettings;
    private final TaskExecutor taskExecutor;

    @Autowired
    public AudioRecognitionService(SpeechSettings speechSettings,
                                   TaskExecutor taskExecutor) {
        this.speechSettings = speechSettings;
        this.taskExecutor = taskExecutor;
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
                log.info(YELLOW);
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

        // Creating microphone input buffer thread

        // Logic for when to end
        // 1. When talking stops and response is received...
        // 2. 3 second time limit before stopping to wait for responses (or no audio? Way to have audio gate in java?).
        //      Resets timer on each response and parses a sentence together
        //      Filter out filler words such as um, uhh, ahh (can be used to extend the timer though)
        //      When doesn't recognize, just say "I didn't get that."
        //4. A final response is compared to commands with a threshold for how related they are.
        //      They must pass the relation threshold to use that command.
        MicBuffer micrunnable = new MicBuffer();
        Thread micThread = new Thread(micrunnable);
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
                                System.out.print(GREEN);
                                System.out.print("\033[2K\r");
                                System.out.printf("%s: %s\n", format.format(correctedTime),
                                        alternative.getTranscript());

                                isFinalEndTime = resultEndTimeInMS;
                                lastTranscriptWasFinal = true;
                            } else {
                                System.out.print(RED);
                                System.out.print("\033[2K\r");
                                System.out.printf("%s: %s", format.format(correctedTime),
                                        alternative.getTranscript());

                                lastTranscriptWasFinal = false;
                            }
                        }

                        public void onComplete() {
                        }

                        public void onError(Throwable t) {
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
                Info targetInfo =
                        new Info(
                                TargetDataLine.class,
                                audioFormat); // Set the system information to read from the microphone audio
                // stream

                if (!AudioSystem.isLineSupported(targetInfo)) {
                    System.out.println("Microphone not supported");
                    System.exit(0);
                }
                // Target data line captures the audio stream the microphone produces.
                targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
                targetDataLine.open(audioFormat);
                micThread.start();

                long startTime = System.currentTimeMillis();

                while (true) {

                    long estimatedTime = System.currentTimeMillis() - startTime;

                    if (lastTranscriptWasFinal || estimatedTime >= STREAMING_LIMIT) {
                        targetDataLine.close();
                        clientStream.closeSend();
                        referenceToStreamController.cancel(); // remove Observer

                        if (resultEndTimeInMS > 0) {
                            finalRequestEndTime = isFinalEndTime;
                        }
                        resultEndTimeInMS = 0;

                        lastAudioInput = null;
                        lastAudioInput = audioInput;
                        audioInput = new ArrayList<ByteString>();
                        break;
                    } else {
                        tempByteString = ByteString.copyFrom(sharedQueue.take());

                        request =
                                StreamingRecognizeRequest.newBuilder()
                                        .setAudioContent(tempByteString)
                                        .build();

                        audioInput.add(tempByteString);

                    }

                    clientStream.send(request);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
