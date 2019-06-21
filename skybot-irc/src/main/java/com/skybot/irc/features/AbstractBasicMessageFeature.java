package com.skybot.irc.features;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.Getter;

@Getter
public abstract class AbstractBasicMessageFeature {
    //TODO Poll voting
    //TODO Spotify Queue add song if queue is on, play endpoint body can add uri list (but it makes new queue each time)
    String trigger;

    String description;

    AbstractBasicMessageFeature(String trigger, String description) {
        this.trigger = trigger;
        this.description = description;
    }

    public abstract void execute(ChannelMessageEvent event);
}
