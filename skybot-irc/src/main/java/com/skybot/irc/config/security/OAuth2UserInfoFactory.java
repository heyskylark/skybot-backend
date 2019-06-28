package com.skybot.irc.config.security;

import com.skybot.irc.exceptions.OAuth2AuthenticationProcessingException;
import com.skybot.irc.models.security.AuthProvider;
import com.skybot.irc.models.security.OAuth2UserInfo;
import com.skybot.irc.models.security.TwitchOAuth2UserInfo;

import java.util.Map;
import java.util.List;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.twitch.toString())) {
            Map convertedAttributes = (Map) ((List) attributes.get("data")).get(0);
            return new TwitchOAuth2UserInfo(convertedAttributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported.");
        }
    }
}
