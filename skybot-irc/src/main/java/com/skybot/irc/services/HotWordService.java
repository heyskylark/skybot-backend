package com.skybot.irc.services;

import ai.kitt.snowboy.SnowboyDetect;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
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

    private OAuth2Credential oAuth2Credential;

    private ITwitchHelixService twitchHelixService;

    private ITwitchApiService twitchApiService;

    public HotWordService (String threadName, SnowboyDetect detector, AudioFormat format, DataLine.Info targetInfo,
                           OAuth2Credential oAuth2Credential, ITwitchHelixService twitchHelixService,
                           ITwitchApiService twitchApiService) {
        super(threadName);

        this.detector = detector;
        this.format = format;
        this.targetInfo = targetInfo;
        this.oAuth2Credential = oAuth2Credential;
        this.twitchHelixService = twitchHelixService;
        this.twitchApiService = twitchApiService;
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
//                    twitchApiService.createStreamMarker(oAuth2Credential.getAccessToken());
                }
            }
        } catch (Exception e) {
            log.error("{}, {}", e, e.getStackTrace());
        }
    }
}
