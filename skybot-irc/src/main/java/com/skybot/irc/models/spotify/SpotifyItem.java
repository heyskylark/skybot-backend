package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Data
public class SpotifyItem {

    public SpotifyItem() { }

    @JsonProperty
    private SpotifyAlbum album;

    @JsonProperty
    private List<SpotifyArtist> artists;

    @JsonProperty(value = "available_markets")
    private List<String> availableMarkets;

    @JsonProperty(value = "disc_number")
    private Integer diskNumber;

    @JsonProperty(value = "duration_ms")
    private Integer durationMs;

    @JsonProperty
    private boolean explicit;

    @JsonProperty(value = "external_ids")
    private Map<String, String> externalIds;

    @JsonProperty(value = "external_urls")
    private Map<String, URI> externalUrls;

    @JsonProperty
    private URI href;

    @JsonProperty
    private String id;

    @JsonProperty(value = "is_local")
    private boolean isLocal;

    @JsonProperty
    private String name;

    @JsonProperty
    private Integer popularity;

    @JsonProperty(value = "preview_url")
    private URI previewUrl;

    @JsonProperty(value = "track_number")
    private Integer trackNumber;

    @JsonProperty
    private String type;

    @JsonProperty
    private String uri;
}
