package com.skybot.irc.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Validated
@Component
@ManagedResource
@ConfigurationProperties(prefix = "skybot")
public class SkyBotProperties {

    public SkyBotProperties() {}

    private Auth auth = new Auth();

    private OAuth2 oauth2 = new OAuth2();

    private boolean debug;

    private boolean voice;

    @NotNull
    private String clientUri;

    @NotNull
    private Map<String, String> bot;

    @NotNull
    private Map<String, String> api;

    @NotNull
    private Map<String, String> credentials;

    @Nullable
    private String nintendoFriendCode;

    private Map<String, List<String>> commands;

    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        public long getTokenExpirationMsec() {
            return tokenExpirationMsec;
        }

        public void setTokenExpirationMsec(long tokenExpirationMsec) {
            this.tokenExpirationMsec = tokenExpirationMsec;
        }
    }

    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }

    public Auth getAuth() {
        return auth;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }
}
