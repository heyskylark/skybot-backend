package com.skybot.irc.services;

public interface ITwitchApiService {

    void createStreamMarker(String authorization);

    void refreshToken(String refreshToken, String clientId, String clientSecret);
}
