package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class SpotifyCurrentPlaybackDevice {

    public SpotifyCurrentPlaybackDevice() { }

    @JsonProperty
    private SpotifyDevice device;

    @JsonProperty(value = "repeat_state")
    private String repeatState;

    @JsonProperty(value = "shuffle_state")
    private Boolean shuffleState;

    @Nullable
    @JsonProperty
    private SpotifyContext context;

    @JsonProperty
    private Long timestamp;

    @Nullable
    @JsonProperty(value = "progress_ms")
    private Integer progressMs;

    @JsonProperty(value = "is_playing")
    private Boolean isPlaying;

    @Nullable
    @JsonProperty
    private SpotifyTrackFull item;

    @JsonProperty(value = "currently_playing_type")
    private String currentlyPlayingType;

    @JsonProperty
    private SpotifyActions actions;
}
