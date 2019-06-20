package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;

@Data
public class SpotifyImage {

    public SpotifyImage() { }

    @JsonProperty
    private Integer height;

    @JsonProperty
    private Integer width;

    @JsonProperty
    private URI url;
}
