package com.skybot.irc.features;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.skybot.irc.config.BotConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NintendoFriendCode extends AbstractBasicMessageFeature {
    private static final String TRIGGER = "!fc";

    private static final String DESCRIPTION = "";

    private String fc_message;

    @Autowired
    public NintendoFriendCode(BotConfiguration botConfiguration) {
        super(TRIGGER, DESCRIPTION);

        if(botConfiguration.getNintendoFriendCode() != null) {
            fc_message = "Nintendo friend code: " + botConfiguration.getNintendoFriendCode();
        }
    }

    @Override
    public void execute(ChannelMessageEvent event){
        log.info("Chat command {} called by: {}", TRIGGER, event.getUser().getName());

        if(fc_message != null) {
            event.getTwitchChat().sendMessage(event.getChannel().getName(), fc_message);
        }
    }
}
