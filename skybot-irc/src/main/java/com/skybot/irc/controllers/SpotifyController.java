package com.skybot.irc.controllers;

import com.skybot.irc.models.spotify.SpotifyToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Base64;

@Slf4j
@Controller
@RequestMapping("/")
public class SpotifyController {

    private static String SPOTIFY_REDIRECT_URI = "http://localhost:8080/login/spotify";
    private static String SPOTIFY_SCOPE = "user-modify-playback-state%20user-read-currently-playing";

    @Autowired
    @Qualifier("spotify")
    private AuthorizationCodeResourceDetails spotify;

    @Autowired
    private SpotifyToken spotifyToken;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/login/spotify")
    public RedirectView loginSpotify(@RequestParam(required = false) String code,
                                     @RequestParam(required = false) String error,
                                     @RequestParam(required = false) String state) {
        if(code == null && error == null) {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(spotify.getUserAuthorizationUri())
                    .queryParam("client_id", spotify.getClientId())
                    .queryParam("response_type", "code")
                    .queryParam("redirect_uri", SPOTIFY_REDIRECT_URI);

            spotify.getScope().forEach(scope -> uriBuilder.queryParam("scope", scope));

            return new RedirectView(uriBuilder.build().toUriString());
        } else {
            URI uri = UriComponentsBuilder
                    .fromUriString(spotify.getAccessTokenUri())
                    .build().toUri();

            String encodedClient = Base64.getEncoder()
                    .encodeToString((spotify.getClientId() + ":" + spotify.getClientSecret()).getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedClient);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "authorization_code");
            map.add("code", code);
            map.add("redirect_uri", SPOTIFY_REDIRECT_URI);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            try {
                ResponseEntity<SpotifyToken> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, SpotifyToken.class);
                spotifyToken.setFromSpotifyToken(responseEntity.getBody());
            } catch (HttpClientErrorException ex) {
                log.error("Error: {}: {}", ex.getMessage(), ex.getResponseBodyAsString());
                ex.printStackTrace();
            }
        }

        return new RedirectView("/");
    }
}
