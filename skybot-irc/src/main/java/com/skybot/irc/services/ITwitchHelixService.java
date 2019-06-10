package com.skybot.irc.services;

import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.helix.domain.User;

public interface ITwitchHelixService {

    User getMe();

    CreateClipList createClip(String streamHostName, boolean isDelayed);
}
