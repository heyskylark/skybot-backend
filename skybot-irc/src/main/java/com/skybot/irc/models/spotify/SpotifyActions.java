package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class SpotifyActions {

    public SpotifyActions() { }

    @JsonProperty
    private Map<String, Boolean> disallows;
}
