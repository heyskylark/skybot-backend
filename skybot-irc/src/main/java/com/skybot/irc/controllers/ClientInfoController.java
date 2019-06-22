package com.skybot.irc.controllers;

import com.skybot.irc.models.websocket.ClientMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ClientInfoController {

//    @MessageMapping("/client.sendMessage")
    @SendTo("/topic/public")
    public ClientMessage clientMessage(@Payload ClientMessage clientMessage) {
        return clientMessage;
    }


}
