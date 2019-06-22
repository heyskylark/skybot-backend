package com.skybot.irc.models.websocket;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

@Data
public class ClientMessage {

    public ClientMessage() { }

    public ClientMessage(MessageType type, String message, URI voiceUri, URI albumArtwork) {
        this.type = type;
        this.message = message;
        this.voiceUri = voiceUri;
        if(type == MessageType.SPOTIFY_ALBUM) {
            this.albumArtwork = albumArtwork;
        }
    }

    public enum MessageType {
        REGULAR,
        START_LISTEN,
        END_LISTEN,
        SPOTIFY_ALBUM
    }

    @NotNull
    private MessageType type;

    @Nullable
    private String message;

    @Nullable
    private URI voiceUri;

    @Nullable
    private URI albumArtwork;
}
