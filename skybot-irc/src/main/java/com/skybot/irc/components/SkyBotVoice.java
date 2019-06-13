package com.skybot.irc.components;

import com.skybot.irc.services.HotWordService;
import com.skybot.irc.services.IAudioRecognitionService;
import com.skybot.irc.services.IVoiceCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkyBotVoice {
    static {
        System.loadLibrary("snowboy-detect-java");
    }

    private final IVoiceCommandService voiceCommandService;

    private final IAudioRecognitionService audioRecognitionService;

    private final TaskExecutor taskExecutor;

    @Autowired
    public SkyBotVoice(IVoiceCommandService voiceCommandService,
                       IAudioRecognitionService audioRecognitionService,
                       TaskExecutor taskExecutor) {
        this.voiceCommandService = voiceCommandService;
        this.audioRecognitionService = audioRecognitionService;
        this.taskExecutor = taskExecutor;
    }

    public void start() {
        log.info("Starting voice component.");
        taskExecutor.execute(new HotWordService(voiceCommandService, audioRecognitionService));
    }
}
