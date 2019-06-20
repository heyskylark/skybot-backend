package com.skybot.irc.services;

import com.skybot.irc.models.spotify.SpotifyCurrentPlaybackDevice;
import com.skybot.irc.models.spotify.SpotifyCurrentlyPlaying;

public interface ISpotifyClientService {

    SpotifyCurrentPlaybackDevice getCurrentlyPlayingDevice();

    SpotifyCurrentlyPlaying getCurrentSong();

    void playSong(String deviceId);

    void pauseSong(String deviceId);

    void nextSong();

    void previousSong();

    void spotifyTokenRefresh();
}
