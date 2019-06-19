package com.skybot.irc.services.impl;

import com.skybot.irc.config.SkyBotProperties;
import com.skybot.irc.models.spotify.SpotifyCurrentSong;
import com.skybot.irc.models.spotify.SpotifyRefresh;
import com.skybot.irc.models.spotify.SpotifyToken;
import com.skybot.irc.services.ISpotifyClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Base64;

@Slf4j
@Service
public class SpotifyClientServiceImpl implements ISpotifyClientService {

    // Deal with client making too many requests, restrict to 3 requests then inform that spotify is down/etc

    private static final String SPOTIFY_SERVICE_URI = "spotify-service-uri";
    private static final String CURRENTLY_PLAYING_URI = "/v1/me/player/currently-playing";
    private static final String PLAY_URI = "/v1/me/player/play";
    private static final String PAUSE_URI = "/v1/me/player/pause";
    private static final String NEXT_URI = "/v1/me/player/next";
    private static final String PREVIOUS_URI = "/v1/me/player/previous";

    private final SpotifyToken spotifyToken;
    private final String spotifyServiceUri;
    private final RestTemplate restTemplate;
    private AuthorizationCodeResourceDetails spotify;

    @Autowired
    public SpotifyClientServiceImpl(SpotifyToken spotifyToken,
                                    SkyBotProperties skyBotProperties,
                                    RestTemplate restTemplate,
                                    @Qualifier("spotify") AuthorizationCodeResourceDetails spotify) {
        this.spotifyToken = spotifyToken;
        this.spotifyServiceUri = skyBotProperties.getApi().get(SPOTIFY_SERVICE_URI);
        this.restTemplate = restTemplate;
        this.spotify = spotify;
    }

    @Override
    public SpotifyCurrentSong getCurrentSong() {
        URI uri = UriComponentsBuilder.fromUriString(spotifyServiceUri + CURRENTLY_PLAYING_URI)
                .build().toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .get(uri)
                .header("Authorization", "Bearer " + spotifyToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON).build();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
            log.info("Does it work: {}: {}", responseEntity.getStatusCode(), responseEntity.getBody());
        } catch (HttpClientErrorException ex) {
            log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                spotifyTokenRefresh();
                playSong();
            } else {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void playSong() {
        URI uri = UriComponentsBuilder.fromUriString(spotifyServiceUri + PLAY_URI)
                .build().toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .put(uri)
                .header("Authorization", "Bearer " + spotifyToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .body("");

        try {
            restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Object.class);
        } catch (HttpClientErrorException ex) {
            log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                spotifyTokenRefresh();
                playSong();
            } else {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void pauseSong() {
        URI uri = UriComponentsBuilder.fromUriString(spotifyServiceUri + PAUSE_URI)
                .build().toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .put(uri)
                .header("Authorization", "Bearer " + spotifyToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .body("");

        try {
            restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Object.class);
        } catch (HttpClientErrorException ex) {
            log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                spotifyTokenRefresh();
                pauseSong();
            } else {
                log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void nextSong() {
        URI uri = UriComponentsBuilder.fromUriString(spotifyServiceUri + NEXT_URI)
                .build().toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .post(uri)
                .header("Authorization", "Bearer " + spotifyToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .body("");

        try {
            restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpClientErrorException ex) {
            log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                spotifyTokenRefresh();
                nextSong();
            } else {
                log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void previousSong() {
        URI uri = UriComponentsBuilder.fromUriString(spotifyServiceUri + PREVIOUS_URI)
                .build().toUri();

        RequestEntity<?> requestEntity = RequestEntity
                .post(uri)
                .header("Authorization", "Bearer " + spotifyToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .body("");

        try {
            restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpClientErrorException ex) {
            log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                spotifyTokenRefresh();
                previousSong();
            } else {
                log.error("Spotify error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void spotifyTokenRefresh() {
        URI uri = UriComponentsBuilder.fromUriString(spotify.getAccessTokenUri())
                .build().toUri();

        String encodedClient = Base64.getEncoder()
                .encodeToString((spotify.getClientId() + ":" + spotify.getClientSecret()).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedClient);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", spotifyToken.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<SpotifyRefresh> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.POST, request, SpotifyRefresh.class);
            spotifyToken.refresh(responseEntity.getBody());
        } catch (HttpClientErrorException ex) {
            log.error("Error refreshing token: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
            ex.printStackTrace();
        }
    }
}
