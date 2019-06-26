package com.skybot.irc.config.security;

import com.skybot.irc.exceptions.OAuth2AuthenticationProcessingException;
import com.skybot.irc.models.security.AuthProvider;
import com.skybot.irc.models.security.OAuth2UserInfo;
import com.skybot.irc.models.security.TwitchOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.twitch.toString())) {
            return new TwitchOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported.");
        }
    }
}
