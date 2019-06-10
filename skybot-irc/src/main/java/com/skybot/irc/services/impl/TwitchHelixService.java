package com.skybot.irc.services.impl;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.CreateClip;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import com.skybot.irc.config.SkyBotProperties;
import com.skybot.irc.services.ITwitchHelixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class TwitchHelixService implements ITwitchHelixService {

    private final TwitchClient twitchClient;
    private final OAuth2Credential oAuth2Credential;
    private final SkyBotProperties botConfiguration;

    private final String IRC_CREDENTIALS_KEY = "irc";

    @Autowired
    public TwitchHelixService(TwitchClient twitchClient,
                              OAuth2Credential oAuth2Credential,
                              SkyBotProperties botConfiguration) {
        this.twitchClient = twitchClient;
        this.oAuth2Credential = oAuth2Credential;
        this.botConfiguration = botConfiguration;
    }

    @Override
    public User getMe() {
        return twitchClient.getHelix().getUsers(oAuth2Credential.getAccessToken(), null, null).execute().getUsers().get(0);
    }

    @Override
    public CreateClipList createClip(String streamHostName, boolean isDelayed) {
        // Get broadcaster id
        // Check of broadcaster is streaming
        // Check if clip is made

        StreamList streams = twitchClient.getHelix().getStreams("", "", 1, null, null, null,
                null, Arrays.asList(streamHostName)).execute();

        if(streams.getStreams().isEmpty()) {
            log.info("Stream not playing.");
        } else {
            Stream stream = streams.getStreams().get(0);
            log.info("Got stream: {} {}", stream.getId(), stream.getTitle());

            try {
                log.info("{}", oAuth2Credential);
                CreateClipList list = twitchClient.getHelix().createClip(oAuth2Credential.getAccessToken(),
                        Long.toString(stream.getId()), isDelayed).execute();

                CreateClip clip = list.getData().get(0);

                log.info("Clip info {} {}", clip.getId(), clip.getEditUrl());

                // Check if clip is created by query getClip with clipId

                return list;
            } catch(Exception e) {
                log.error("{} {}", e, e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }
}
