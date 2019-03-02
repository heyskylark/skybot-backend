package com.skybot.voice;

import ai.kitt.snowboy.SnowboyDetect;
import com.skybot.voice.service.HotWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

@Slf4j
@Component
public class SkyBotVoice {
    static {
        System.loadLibrary("snowboy-detect-java");
    }

    private SnowboyDetect detector;

    public SkyBotVoice() {
        detector = new SnowboyDetect(
                "resources/common.res",
                "resources/models/snowboy.umdl"
        );

        detector.SetSensitivity("0.5");
        detector.SetAudioGain(1);
        detector.ApplyFrontend(false);
    }

    public void start() {
        log.info("Starting voice component...");

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);

        HotWordService hotWordService = new HotWordService("Snowboy Service", detector, format, targetInfo);
        hotWordService.start();
    }
}
