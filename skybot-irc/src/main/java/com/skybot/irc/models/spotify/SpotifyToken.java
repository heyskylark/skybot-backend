package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class SpotifyToken {
    public SpotifyToken() { }

    @NotNull
    @JsonProperty(value = "access_token")
    private String accessToken;

    @NotNull
    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty
    private String scope;

    @NotNull
    @JsonProperty(value = "expires_in")
    private Integer expiresIn;

    @NotNull
    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    public void setFromSpotifyToken(SpotifyToken spotifyToken) {
        this.accessToken = spotifyToken.getAccessToken();
        this.tokenType = spotifyToken.getTokenType();
        this.scope = spotifyToken.getScope();
        this.expiresIn = spotifyToken.getExpiresIn();
        this.refreshToken = spotifyToken.getRefreshToken();
    }

    public void refresh(SpotifyRefresh spotifyRefresh) {
        this.accessToken = spotifyRefresh.getAccessToken();
        this.tokenType = spotifyRefresh.getTokenType();
        this.scope = spotifyRefresh.getScope();
        this.expiresIn = spotifyRefresh.getExpiresIn();
    }
}
