package com.skybot.irc.config;

import com.skybot.irc.models.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDataConfig {
    @Bean
    UserPrincipal userPrincipal() {
        return new UserPrincipal();
    }
}
