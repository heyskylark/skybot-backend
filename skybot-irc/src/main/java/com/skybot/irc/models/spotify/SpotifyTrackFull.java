package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Data
public class SpotifyTrackFull {

    public SpotifyTrackFull() { }

    @JsonProperty
    public SpotifyAlbumSimplified album;

    @JsonProperty
    public List<SpotifyArtistSimplified> artists;

    @JsonProperty(value = "available_markets")
    public String availableMarkets;

    @JsonProperty(value = "disc_number")
    public Integer diskNumber;

    @JsonProperty(value = "duration_ms")
    public Integer durationMs;

    @JsonProperty
    public Boolean explicit;

    @JsonProperty(value = "external_ids")
    public Map<String, String> externalIds;

    @JsonProperty(value = "external_urls")
    public Map<String, URI> externalUrls;

    @JsonProperty
    public URI href;

    @JsonProperty
    public String id;

    @JsonProperty(value = "is_playable")
    public Boolean isPlayable;

    @JsonProperty(value = "linked_from")
    public SpotifyLinkedForm linkedForm;

    @JsonProperty
    public Map<String, String> restrictions;

    @JsonProperty
    public String name;

    @JsonProperty
    public Integer popularity;

    @Nullable
    @JsonProperty(value = "preview_url")
    public URI previewUrl;

    @JsonProperty(value = "track_number")
    public Integer trackNumber;

    @JsonProperty
    public String type;

    @JsonProperty
    public URI uri;

    @JsonProperty(value = "is_local")
    public Boolean isLocal;
}
