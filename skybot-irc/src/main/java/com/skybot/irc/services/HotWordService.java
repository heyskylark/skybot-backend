package com.skybot.irc.services;

import ai.kitt.snowboy.SnowboyDetect;
import com.github.twitch4j.helix.domain.CreateClipList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
public class HotWordService extends Thread {

    private final SnowboyDetect detector;

    private final AudioFormat format;

    private final DataLine.Info targetInfo;

    private final IVoiceCommandService voiceCommandService;

    private final String CHANNEL;

    public HotWordService (String threadName,
                           SnowboyDetect detector,
                           AudioFormat format,
                           DataLine.Info targetInfo,
                           IVoiceCommandService voiceCommandService,
                           String channel) {
        super(threadName);

        this.detector = detector;
        this.format = format;
        this.targetInfo = targetInfo;
        this.voiceCommandService = voiceCommandService;
        this.CHANNEL = channel;
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
//                    voiceCommandService.createClipAndShare(CHANNEL);
                }
            }
        } catch (Exception e) {
            log.error("{}, {}", e, e.getStackTrace());
        }
    }
}
