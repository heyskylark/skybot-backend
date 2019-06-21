package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;
import java.util.Map;

@Data
public class SpotifyArtistSimplified {

    public SpotifyArtistSimplified() { }

    @JsonProperty(value = "external_urls")
    public Map<String, URI> externalUrls;

    @JsonProperty
    public URI href;

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String type;

    @JsonProperty
    public String uri;
}
