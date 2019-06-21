package com.skybot.irc.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpotifyCopyright {

    public SpotifyCopyright() { }

    @JsonProperty
    private String text;

    @JsonProperty
    private String type;
}
