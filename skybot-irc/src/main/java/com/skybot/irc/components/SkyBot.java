package com.skybot.irc;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.skybot.irc.config.BotConfiguration;
import com.skybot.irc.features.AbstractBasicMessageFeature;
import com.skybot.irc.features.NintendoFriendCode;
import com.skybot.voice.SkyBotVoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SkyBot {
    private final String TWITCH_CLIENT_ID_KEY = "twitch_client_id";
    private final String TWITCH_CLIENT_SECRET_KEY = "twitch_client_secret";
    private final String IRC_CREDENTIALS_KEY = "irc";

    private SkyBotVoice voice;

    private BotConfiguration botConfiguration;

    private TwitchClient twitchClient;

    private NintendoFriendCode nintendoFriendCode;

    private List<AbstractBasicMessageFeature> messageFeatures;

    @Autowired
    public SkyBot(BotConfiguration botConfiguration, SkyBotVoice voice, NintendoFriendCode nintendoFriendCode) {
        log.info("Initializing SkyBot...");

        this.botConfiguration = botConfiguration;
        this.voice = voice;
        this.nintendoFriendCode = nintendoFriendCode;

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                botConfiguration.getCredentials().get(IRC_CREDENTIALS_KEY)
        );

        twitchClient = clientBuilder
                .withClientId(botConfiguration.getApi().get(TWITCH_CLIENT_ID_KEY))
                .withClientSecret(botConfiguration.getApi().get(TWITCH_CLIENT_SECRET_KEY))
                .withChatAccount(credential)
                .withEnableChat(true)
                .build();

        registerFeatures();

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> onChannelMessage(event));

        if(botConfiguration.getVoice()) {
            this.voice.start();
        }

        start();
    }

    private void start() {
        for(String channel: botConfiguration.getChannels()) {
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
