package com.skybot.irc.components;

import com.skybot.irc.services.HotWordService;
import com.skybot.irc.services.IAudioRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkyBotVoice {
    static {
        System.loadLibrary("snowboy-detect-java");
    }

    private final IAudioRecognitionService audioRecognitionService;

    private final TaskExecutor executor;

    @Autowired
    public SkyBotVoice(IAudioRecognitionService audioRecognitionService,
                       @Qualifier("mainTaskExecutor") TaskExecutor executor) {
        this.audioRecognitionService = audioRecognitionService;
        this.executor = executor;
    }

    void start() {
        log.info("Starting voice component.");
        executor.execute(new HotWordService(audioRecognitionService));
    }
}
