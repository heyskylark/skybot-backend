package com.skybot.irc.services;

import com.skybot.irc.models.spotify.SpotifyCurrentPlaybackDevice;
import com.skybot.irc.models.spotify.SpotifyCurrentlyPlaying;

import java.util.List;

public interface ISpotifyClientService {

    SpotifyCurrentPlaybackDevice getCurrentlyPlayingDevice();

    SpotifyCurrentlyPlaying getCurrentSong();

    void playSong(String deviceId, List<String> songUris);

    void pauseSong(String deviceId);

    void nextSong();

    void previousSong();

    void spotifyTokenRefresh();
}
