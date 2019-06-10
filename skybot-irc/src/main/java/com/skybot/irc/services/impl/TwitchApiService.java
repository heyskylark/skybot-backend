package com.skybot.irc.services.impl;

import com.github.twitch4j.helix.domain.User;
import com.skybot.irc.services.ITwitchApiService;
import com.skybot.irc.services.ITwitchHelixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TwitchApiService implements ITwitchApiService {

    private final ITwitchHelixService twitchHelixService;
    private final RestTemplate restTemplate;

    private final String ID_URI = "https://id.twitch.tv";
    private final String HELIX_URI = "https://api.twitch.tv/helix";

    @Autowired
    public TwitchApiService(ITwitchHelixService twitchHelixService) {
        this.twitchHelixService = twitchHelixService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void createStreamMarker(String authorization) {
        User user = twitchHelixService.getMe();

        final String uri = HELIX_URI + "/streams/markers";

        final String body = "{\"user_id\": "+user.getId()+"}";

        HttpEntity<?> httpEntity = new HttpEntity<>(body, createHeader(MediaType.APPLICATION_JSON, authorization));

        try {
            restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        } catch(HttpClientErrorException ex) {
            log.error("{}", ex.getResponseBodyAsString());
        }
    }

    @Override
    public void refreshToken(String refreshToken, String clientId, String clientSecret) {
        final String uri = ID_URI + "/oauth2/token"
                + "?grant_type=refresh_token&refresh_token= " + refreshToken
                + "&client_id=" + clientId + "&client_secret=" + clientSecret;

        HttpEntity<?> httpEntity = new HttpEntity<>(null, createHeader(MediaType.APPLICATION_JSON, null));

        try {
            restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        } catch(HttpClientErrorException ex) {
            log.error("{}", ex.getResponseBodyAsString());
        }
    }

    private HttpHeaders createHeader(MediaType mediaType, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        if(authorization != null) {
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + authorization);
        }

        return headers;
    }
}
