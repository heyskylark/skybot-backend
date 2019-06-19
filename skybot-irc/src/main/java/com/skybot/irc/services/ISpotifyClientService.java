package com.skybot.irc.services;

import com.skybot.irc.models.spotify.SpotifyCurrentSong;

public interface ISpotifyClientService {

    SpotifyCurrentSong getCurrentSong();

    void playSong();

    void pauseSong();

    void nextSong();

    void previousSong();

    void spotifyTokenRefresh();
}
