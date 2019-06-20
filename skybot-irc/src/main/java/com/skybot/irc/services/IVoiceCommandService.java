package com.skybot.irc.services;

public interface IVoiceCommandService {

    void findCommand(String command);

    void createClipSelfAndShare();

    void getCurrentlyPlayingSongAndShare();
}
