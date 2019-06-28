package com.skybot.irc.services.impl;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.CreateClip;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.helix.domain.User;
import com.skybot.irc.services.ITwitchHelixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwitchHelixService implements ITwitchHelixService {

    private final TwitchClient twitchClient;

    @Autowired
    public TwitchHelixService(TwitchClient twitchClient) {
        this.twitchClient = twitchClient;
    }

    @Override
    public User getMe() {
        return twitchClient.getHelix().getUsers("stateless-need-token-sent", null, null)
                .execute().getUsers().get(0);
    }

    @Override
    public CreateClipList createClipSelf(boolean isDelayed) {
        // Check of broadcaster is streaming
        User user = getMe();
        log.debug("Got user: {} {}", user.getId(), user.getDisplayName());

        try {
            CreateClipList list = twitchClient.getHelix().createClip("stateless-need-token-sent",
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
