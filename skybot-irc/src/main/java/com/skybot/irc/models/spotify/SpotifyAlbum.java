package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class SpotifyAlbum {

    public SpotifyAlbum() { }

    @JsonProperty(value = "album_type")
    private String albumType;

    @JsonProperty
    private List<SpotifyArtist> artists;

    @JsonProperty(value = "available_markets")
    private List<String> availableMarkets;

    @JsonProperty(value = "external_urls")
    private Map<String, URI> externalUrls;

    @JsonProperty
    private URI href;

    @JsonProperty
    private String id;

    @JsonProperty
    private List<SpotifyImage> images;

    @JsonProperty
    private String name;

    @JsonProperty(value = "release_date")
    private Date releaseDate;

    @JsonProperty(value = "release_date_precision")
    private String releaseDatePrecision;

    @JsonProperty(value = "total_tracks")
    private Integer totalTracks;

    @JsonProperty
    private String type;

    @JsonProperty
    private String uri;
}
