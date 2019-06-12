package com.skybot.irc.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserPrincipal {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String login;

    @JsonProperty(value = "display_name")
    private String userName;

    @JsonProperty
    private String type;

    @JsonProperty(value = "broadcaster_type")
    private String broadcasterType;

    @JsonProperty
    private String description;

    @JsonProperty(value = "profile_image_url")
    private String profileImageUrl;

    @JsonProperty(value = "offline_image_url")
    private String offlineImageUrl;

    @JsonProperty(value = "view_count")
    private String viewCount;
}
