package com.skybot.irc.services.impl;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.CreateClip;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.helix.domain.User;
import com.skybot.irc.config.SkyBotProperties;
import com.skybot.irc.services.ITwitchHelixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwitchHelixService implements ITwitchHelixService {

    private final TwitchClient twitchClient;
    private final OAuth2Credential oAuth2Credential;

    private final String IRC_CREDENTIALS_KEY = "irc";

    @Autowired
    public TwitchHelixService(TwitchClient twitchClient,
                              OAuth2Credential oAuth2Credential,
                              SkyBotProperties botConfiguration) {
        this.twitchClient = twitchClient;
        this.oAuth2Credential = oAuth2Credential;
    }

    @Override
    public User getMe() {
        return twitchClient.getHelix().getUsers(oAuth2Credential.getAccessToken(), null, null)
                .execute().getUsers().get(0);
    }

    @Override
    public CreateClipList createClipSelf(boolean isDelayed) {
        // Check of broadcaster is streaming
        User user = getMe();
        log.debug("Got user: {} {}", user.getId(), user.getDisplayName());

        try {
            CreateClipList list = twitchClient.getHelix().createClip(oAuth2Credential.getAccessToken(),
                    Long.toString(user.getId()), isDelayed).execute();

            CreateClip clip = list.getData().get(0);

            log.info("Clip info {} {}", clip.getId(), clip.getEditUrl());

            return list;
        } catch(Exception e) {
            log.error("{} {}", e, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
