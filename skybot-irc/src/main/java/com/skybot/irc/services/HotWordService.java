package com.skybot.irc.services;

import ai.kitt.snowboy.SnowboyDetect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
@Component
public class HotWordService implements Runnable {

    private final IAudioRecognitionService audioRecognitionService;

    private final SnowboyDetect detector;

    private final AudioFormat format;

    private final DataLine.Info targetInfo;

    public HotWordService (IAudioRecognitionService audioRecognitionService) {
        detector = new SnowboyDetect(
                "resources/common.res",
                "resources/models/snowboy.umdl"
        );

        detector.SetSensitivity("0.5");
        detector.SetAudioGain(1);
        detector.ApplyFrontend(false);

        this.format = new AudioFormat(16000, 16, 1, true, false);
        this.targetInfo = new DataLine.Info(TargetDataLine.class, format);

        this.audioRecognitionService = audioRecognitionService;
    }

    public void run() {
        try {
            TargetDataLine targetLine =
                    (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetLine.open(format);
            targetLine.start();

            // Reads 0.1 second of audio in each call.
            byte[] targetData = new byte[3200];
            short[] snowboyData = new short[1600];
            int numBytesRead;

            while (true) {
                // Reads the audio data in the blocking mode. If you are on a very slow
                // machine such that the hotword detector could not process the audio
                // data in real time, this will cause problem...
                numBytesRead = targetLine.read(targetData, 0, targetData.length);

                if (numBytesRead == -1) {
                    log.error("Fails to read audio data.");
                    break;
                }

                // Converts bytes into int16 that Snowboy will read.
                ByteBuffer.wrap(targetData).order(
                        ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(snowboyData);

                // Detection.
                int result = detector.RunDetection(snowboyData, snowboyData.length);
                if (result > 0) {
                    log.info("Hotword {} detected!", result);
                    try {
                        audioRecognitionService.streamingRecognize();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            log.error("{}, {}", e, e.getStackTrace());
        }
    }
}
