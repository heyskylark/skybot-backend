package com.skybot.voice.service;

import ai.kitt.snowboy.SnowboyDetect;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
public class HotWordService extends Thread {

    private SnowboyDetect detector;

    private AudioFormat format;

    private DataLine.Info targetInfo;

    public HotWordService (String s, SnowboyDetect detector, AudioFormat format, DataLine.Info targetInfo) {
        super(s);

        this.detector = detector;
        this.format = format;
        this.targetInfo = targetInfo;
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
                    System.out.print("Fails to read audio data.");
                    break;
                }

                // Converts bytes into int16 that Snowboy will read.
                ByteBuffer.wrap(targetData).order(
                        ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(snowboyData);

                // Detection.
                int result = detector.RunDetection(snowboyData, snowboyData.length);
                if (result > 0) {
                    System.out.print("Hotword " + result + " detected!\n");
                }
            }
        } catch (Exception e) {
            log.error("{}, {}", e, e.getStackTrace());
        }
    }
}
