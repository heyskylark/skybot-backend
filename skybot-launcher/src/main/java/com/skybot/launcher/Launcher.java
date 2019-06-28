package com.skybot.launcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication(
        scanBasePackages={"com.skybot.irc.*"}
)
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = {
        "com.skybot.irc.models",
        "com.skybot.irc.repositories"
})
@EntityScan("com.skybot.irc.models")
public class Launcher {

    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }
}
