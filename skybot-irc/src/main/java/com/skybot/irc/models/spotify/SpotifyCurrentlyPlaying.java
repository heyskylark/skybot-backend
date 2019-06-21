package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class SpotifyCurrentlyPlaying {

    public SpotifyCurrentlyPlaying() { }

    @Nullable
    @JsonProperty
    private SpotifyContext context;

    @NotNull
    @JsonProperty
    private Long timestamp;

    @Nullable
    @JsonProperty(value = "progress_ms")
    private Integer progressMs;

    @NotNull
    @JsonProperty(value = "currently_playing_type")
    private String currentlyPlayingType;

    @NotNull
    @JsonProperty(value = "is_playing")
    private boolean isPlaying;

    @Nullable
    @JsonProperty
    private SpotifyTrackFull item;

    @NotNull
    @JsonProperty
    private SpotifyActions actions;
}
