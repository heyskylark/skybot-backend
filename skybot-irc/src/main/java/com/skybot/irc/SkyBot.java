package com.skybot.irc;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.skybot.irc.config.BotConfiguration;
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

    private BotConfiguration botConfiguration;

    private TwitchClient twitchClient;

    private NintendoFriendCode nintendoFriendCode;

    private List<AbstractBasicMessageFeature> messageFeatures;

    @Autowired
    public SkyBot(BotConfiguration botConfiguration, NintendoFriendCode nintendoFriendCode) {
        log.info("Initializing SkyBot...");

        this.botConfiguration = botConfiguration;
        this.nintendoFriendCode = nintendoFriendCode;

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                botConfiguration.getCredentials().get("irc")
        );

        twitchClient = clientBuilder
//                .withClientId(botConfiguration.getApi().get("twitch_client_id"))
//                .withClientSecret(botConfiguration.getApi().get("twitch_client_secret"))
                .withChatAccount(credential)
                .withEnableChat(true)
                .build();

        registerFeatures();

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> onChannelMessage(event));

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
