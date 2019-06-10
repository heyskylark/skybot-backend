package com.skybot.irc.config;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Configuration
public class TwitchConfiguration {

    private final String TWITCH_CLIENT_ID_KEY = "twitch_client_id";
    private final String TWITCH_CLIENT_SECRET_KEY = "twitch_client_secret";
    private final String HELIX_CREDENTIALS_KEY = "helix";
    private final String HELIX_CREDENTIALS_REFRESH_KEY = "helix_refresh";

    @Autowired
    SkyBotProperties skyBotProperties;

    @Bean
    public TwitchClient twitchClient() {
        log.info("Configuring twitch client.");

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                skyBotProperties.getCredentials().get(HELIX_CREDENTIALS_KEY)
        );

        return clientBuilder
                .withClientId(skyBotProperties.getApi().get(TWITCH_CLIENT_ID_KEY))
                .withClientSecret(skyBotProperties.getApi().get(TWITCH_CLIENT_SECRET_KEY))
                .withChatAccount(credential)
                .withEnableChat(true)
                .withEnableHelix(true)
                .build();
    }

    @Bean
    public OAuth2Credential oAuth2Credential() {
        OAuth2Credential oAuth2Credential = new OAuth2Credential("twitch",
                skyBotProperties.getCredentials().get(HELIX_CREDENTIALS_KEY),
                skyBotProperties.getCredentials().get(HELIX_CREDENTIALS_REFRESH_KEY), null, null, null, null);
        TwitchIdentityProvider twitchIdentityProvider = new TwitchIdentityProvider("twitch",
                skyBotProperties.getApi().get(TWITCH_CLIENT_ID_KEY),
                skyBotProperties.getApi().get(TWITCH_CLIENT_SECRET_KEY));
        return twitchIdentityProvider.getAdditionalCredentialInformation(oAuth2Credential).orElseThrow(
                () -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Helix OAuth token may be bad")
        );
    }
}
