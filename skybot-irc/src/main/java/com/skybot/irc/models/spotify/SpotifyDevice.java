package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpotifyDevice {

    public SpotifyDevice() { }

    @JsonProperty
    private String id;

    @JsonProperty(value = "is_active")
    private Boolean isActive;

    @JsonProperty(value = "is_restricted")
    private Boolean isRestricted;

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty(value = "volume_percent")
    private Integer volumePercent;
}
