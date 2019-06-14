package com.skybot.irc.services;

public interface IVoiceCommandService {

    void findCommand(String command);

    void createClipAndShare(String channel);

    void check();
}
