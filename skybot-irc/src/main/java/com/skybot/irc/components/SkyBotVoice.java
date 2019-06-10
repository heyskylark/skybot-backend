package com.skybot.irc.components;

import ai.kitt.snowboy.SnowboyDetect;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.skybot.irc.services.HotWordService;
import com.skybot.irc.services.ITwitchApiService;
import com.skybot.irc.services.ITwitchHelixService;
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

    private SnowboyDetect detector;

    private OAuth2Credential oAuth2Credential;

    private ITwitchHelixService twitchHelixService;

    private ITwitchApiService twitchApiService;

    @Autowired
    public SkyBotVoice(OAuth2Credential oAuth2Credential,
                       ITwitchHelixService twitchHelixService,
                       ITwitchApiService twitchApiService) {
        detector = new SnowboyDetect(
                "resources/common.res",
                "resources/models/snowboy.umdl"
        );

        detector.SetSensitivity("0.5");
        detector.SetAudioGain(1);
        detector.ApplyFrontend(false);

        this.oAuth2Credential = oAuth2Credential;
        this.twitchHelixService = twitchHelixService;
        this.twitchApiService = twitchApiService;
    }

    public void start() {
        log.info("Starting voice component.");

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);

        HotWordService hotWordService = new HotWordService("Snowboy Service", detector, format, targetInfo,
                oAuth2Credential, twitchHelixService, twitchApiService);
        hotWordService.start();
    }
}
