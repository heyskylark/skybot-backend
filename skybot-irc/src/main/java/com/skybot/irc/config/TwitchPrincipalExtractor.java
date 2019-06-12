package com.skybot.irc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.List;
import java.util.Map;

@Slf4j
public class TwitchPrincipalExtractor implements PrincipalExtractor {

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        return ((List) map.get("data")).get(0);
    }
}
