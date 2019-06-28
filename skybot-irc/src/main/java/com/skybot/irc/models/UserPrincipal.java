package com.skybot.irc.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
public class UserPrincipal {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String login;

    @JsonProperty(value = "display_name")
    private String name;

    @JsonProperty
    private String email;

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

    @JsonProperty
    List<GrantedAuthority> authorities;

    @JsonIgnore
    private String password;

    public UserPrincipal() { }

    public UserPrincipal(Object userJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserPrincipal userPrincipal = objectMapper.convertValue(userJson,UserPrincipal.class);

        this.id = userPrincipal.getId();
        this.login = userPrincipal.getLogin();
        this.name = userPrincipal.getName();
        this.type = userPrincipal.getType();
        this.broadcasterType = userPrincipal.getBroadcasterType();
        this.description = userPrincipal.getDescription();
        this.profileImageUrl = userPrincipal.getProfileImageUrl();
        this.offlineImageUrl = userPrincipal.getOfflineImageUrl();
        this.viewCount = userPrincipal.getViewCount();
    }

    public void setFromJson(Object userJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserPrincipal userPrincipal = objectMapper.convertValue(userJson,UserPrincipal.class);

        this.id = userPrincipal.getId();
        this.login = userPrincipal.getLogin();
        this.name = userPrincipal.getName();
        this.type = userPrincipal.getType();
        this.broadcasterType = userPrincipal.getBroadcasterType();
        this.description = userPrincipal.getDescription();
        this.profileImageUrl = userPrincipal.getProfileImageUrl();
        this.offlineImageUrl = userPrincipal.getOfflineImageUrl();
        this.viewCount = userPrincipal.getViewCount();
    }
}
