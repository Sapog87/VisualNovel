package com.sapog87.visual_novel.front.adapter;

import com.pengrad.telegrambot.model.Message;
import com.sapog87.visual_novel.interpreter.data.Data;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMessage {
    Long userId;
    String callbackData;
    boolean startFlag;
    boolean restartFlag;
    Data data;
    Message message;
}
