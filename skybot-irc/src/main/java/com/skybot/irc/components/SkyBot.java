package com.skybot.irc.components;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.skybot.irc.config.SkyBotProperties;
import com.skybot.irc.features.AbstractBasicMessageFeature;
import com.skybot.irc.features.NintendoFriendCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SkyBot {

    private SkyBotVoice voice;

    private SkyBotProperties skyBotProperties;

    private TwitchClient twitchClient;

    private NintendoFriendCode nintendoFriendCode;

    private List<AbstractBasicMessageFeature> messageFeatures;

    @Autowired
    public SkyBot(SkyBotProperties skyBotProperties,
                  SkyBotVoice voice,
                  NintendoFriendCode nintendoFriendCode,
                  TwitchClient twitchClient) {
        log.info("Initializing SkyBot.");

        this.twitchClient = twitchClient;
        this.skyBotProperties = skyBotProperties;
        this.voice = voice;
        this.nintendoFriendCode = nintendoFriendCode;

        registerFeatures();

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> onChannelMessage(event));

        if(skyBotProperties.isVoice()) {
            this.voice.start();
        }

        start();
    }

    private void start() {
        for(String channel: skyBotProperties.getChannels()) {
            twitchClient.getChat().joinChannel(channel);

            log.info("Joined channel {}", channel);
        }
    }

    private void registerFeatures() {
        messageFeatures = new ArrayList<>();
        messageFeatures.add(nintendoFriendCode);
    }

    private void onChannelMessage(ChannelMessageEvent event) {
        for(AbstractBasicMessageFeature messageFeature : messageFeatures) {
            if(messageFeature.getTrigger().equals(event.getMessage().trim())) {
                messageFeature.execute(event);
            }
        }
    }
}
