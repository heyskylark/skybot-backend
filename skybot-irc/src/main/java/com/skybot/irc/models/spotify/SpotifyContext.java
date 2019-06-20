package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;
import java.util.Map;

@Data
public class SpotifyContext {

    public SpotifyContext() { }

    @JsonProperty(value = "external_urls")
    private Map<String, URI> externalUrls;

    @JsonProperty
    private URI href;

    @JsonProperty
    private String type;

    @JsonProperty
    private String uri;
}
