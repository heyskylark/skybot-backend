package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class SpotifyRefresh {
    public SpotifyRefresh() { }

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
}
