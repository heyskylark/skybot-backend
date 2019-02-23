package com.skybot.irc.config;

import com.skybot.irc.utility.JsonPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@PropertySource(
        value = "classpath:botConfig.json",
        factory = JsonPropertySourceFactory.class
)
@ConfigurationProperties
public class BotConfiguration {

    public Boolean debug;

    private Map<String, String> bot;

    private Map<String, String> api;

    private Map<String, String> credentials;

    private List<String> channels;

    private String nintendoFriendCode;

    @Override
    public String toString() {
        return "com.skybot.irc.config.BotConfiguration {" +
                "bot=" + bot +
                ", api=" + api +
                ", credentials=" + credentials +
                ", channels=" + channels +
                " }";
    }
}
