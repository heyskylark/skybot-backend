package com.skybot.irc.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Data
@Validated
@Component
@ManagedResource
@ConfigurationProperties(prefix = "skybot")
public class SkyBotProperties {

    private boolean debug;

    private boolean voice;

    @NotNull
    private Map<String, String> bot;

    @NotNull
    private Map<String, String> api;

    @NotNull
    private Map<String, String> credentials;

    private String nintendoFriendCode;
}
