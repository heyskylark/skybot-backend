package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;
import java.util.Map;

@Data
public class SpotifyArtist {

    public SpotifyArtist() { }

    @JsonProperty(value = "external_urls")
    private Map<String, URI> externalUrls;

    @JsonProperty
    private URI href;

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty
    private String uri;
}
