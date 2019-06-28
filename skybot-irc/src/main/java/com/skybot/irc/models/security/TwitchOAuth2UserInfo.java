package com.skybot.irc.models.security;

import java.util.Map;

public class TwitchOAuth2UserInfo extends OAuth2UserInfo {

    public TwitchOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getLogin() {
        return (String) attributes.get("login");
    }

    @Override
    public String getName() {
        return (String) attributes.get("display_name");
    }

    @Override
    public String getUserType() {
        return (String) attributes.get("type");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image_url");
    }

}
