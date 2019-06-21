package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class SpotifyAlbumSimplified {

    public SpotifyAlbumSimplified() { }

    @Nullable
    @JsonProperty(value = "album_group")
    public String albumGroup;

    @JsonProperty(value = "album_type")
    public String albumType;

    @JsonProperty
    public SpotifyArtistSimplified artists;

    @JsonProperty(value = "available_markets")
    public List<String> availableMarkets;

    @JsonProperty(value = "external_urls")
    public Map<String, URI> externalUrls;

    @JsonProperty
    public URI href;

    @JsonProperty
    public String id;

    @JsonProperty
    public List<SpotifyImage> images;

    @JsonProperty
    public String name;

    @JsonProperty(value = "release_date")
    public Date releaseDate;

    @JsonProperty(value = "release_date_precision")
    public String releaseDatePrecision;

    @JsonProperty
    public Map<String, String> restrictions;

    @JsonProperty
    public String type;

    @JsonProperty
    public String uri;
}
