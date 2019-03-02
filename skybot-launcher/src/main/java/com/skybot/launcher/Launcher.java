package com.skybot.launcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@Slf4j
@SpringBootApplication(
        scanBasePackages={
                "com.skybot.irc",
                "com.skybot.voice"
        }
)
@EnableAutoConfiguration
public class Launcher {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Launcher.class).run(args);
    }
}
