package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class SpotifyPlayerError {

    public enum ErrorReason {
        NO_PREV_TRACK,
        NO_NEXT_TRACK,
        NO_SPECIFIC_TRACK,
        ALREADY_PAUSED,
        NOT_PAUSED,
        NOT_PLAYING_LOCALLY,
        NOT_PLAYING_TRACK,
        NOT_PLAYING_CONTEXT,
        ENDLESS_CONTEXT,
        CONTEXT_DISALLOW,
        ALREADY_PLAYING,
        RATE_LIMITED,
        REMOTE_CONTROL_DISALLOW,
        DEVICE_NOT_CONTROLLABLE,
        VOLUME_CONTROL_DISALLOW,
        NO_ACTIVE_DEVICE,
        PREMIUM_REQUIRED,
        UNKNOWN
    }

    public SpotifyPlayerError() { }

    @JsonProperty
    public HttpStatus status;

    @JsonProperty
    public String message;

    @JsonProperty
    public ErrorReason reason;
}
