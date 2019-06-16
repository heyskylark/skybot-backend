package com.skybot.irc.utility;

public enum VoiceCommandKeys {
    CLIP("clip"),
    START_POLL("start-poll"),
    END_POLL("end-poll"),
    SONG_INFO("song"),
    NEXT_SONG("next-song"),
    PREV_SONG("previous-song"),
    PLAY_SONG("play"),
    PAUSE_SONG("pause"),
    NO_COMMAND("no-command");

    private String commandKey;

    VoiceCommandKeys(String commandKey) {
        this.commandKey = commandKey;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public static VoiceCommandKeys resolve(String displayString) {
        for(VoiceCommandKeys type : VoiceCommandKeys.values()) {
            if(type.getCommandKey().equals(displayString)) {
                return type;
            }
        }
        return NO_COMMAND;
    }
}
