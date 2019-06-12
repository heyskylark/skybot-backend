package com.skybot.irc.components;

import ai.kitt.snowboy.SnowboyDetect;
import com.skybot.irc.services.HotWordService;
import com.skybot.irc.services.IVoiceCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SnowboyDetect detector;

    private final IVoiceCommandService voiceCommandService;

    @Autowired
    public SkyBotVoice(IVoiceCommandService voiceCommandService) {
        detector = new SnowboyDetect(
                "resources/common.res",
                "resources/models/snowboy.umdl"
        );

        detector.SetSensitivity("0.5");
        detector.SetAudioGain(1);
        detector.ApplyFrontend(false);

        this.voiceCommandService = voiceCommandService;
    }

    public void start(String channel) {
        log.info("Starting voice component.");

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);

        HotWordService hotWordService = new HotWordService("Snowboy Service", detector, format, targetInfo,
               voiceCommandService, channel);
        hotWordService.start();
    }
}
