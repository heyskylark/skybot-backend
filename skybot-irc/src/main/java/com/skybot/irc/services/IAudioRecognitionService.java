package com.skybot.irc.services;

public interface IAudioRecognitionService {

    /**
     * Peforms audio stream recognition using google speech-to-text
     */
    void streamingRecognize() throws Exception;
}
