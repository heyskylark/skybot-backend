package com.skybot.irc.services.impl;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.exception.NotFoundException;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.skybot.irc.services.ITwitchHelixService;
import com.skybot.irc.services.IVoiceCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Slf4j
@Service
public class VoiceCommandService implements IVoiceCommandService {

    private final ITwitchHelixService twitchHelixService;
    private final TwitchClient twitchClient;

    @Autowired
    public VoiceCommandService(ITwitchHelixService twitchHelixService,
                               TwitchClient twitchClient) {
        this.twitchHelixService = twitchHelixService;
        this.twitchClient = twitchClient;
    }

    @Override
    public void createClipAndShare(String channel) {
        try {
            CreateClipList createClipList = twitchHelixService.createClipSelf(false);

            if (!createClipList.getData().isEmpty()) {
                createClipList.getData().forEach(clip -> {
                    int indexOfEdit = clip.getEditUrl().lastIndexOf("/edit");
                    String noEditClipUrl = clip.getEditUrl().substring(0, indexOfEdit);
                    twitchClient.getChat().sendMessage(channel, noEditClipUrl);

                    log.debug("Sent clip url [{}] to channel [{}] chat", noEditClipUrl, channel);
                });
                // Send confirmation and voice audio through websocket to live client
            } else {
                log.error("There was a problem creating the clip, no clips were made.");
                // Websocket "Problem Creating clip"
            }
        } catch(HystrixRuntimeException ex) {
            log.error("ex {}", ex);
            log.error("Error creating clip: {}", ex.getFailureType());
            // Websocket "Problem Creating clip"
        } catch(NotFoundException ex) {
            log.error("ex {}", ex);
        }
    }

    public void check() {
        log.info("TEST");
    }
}
