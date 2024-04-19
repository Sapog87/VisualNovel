package com.sapog87.visual_novel.front;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageData {
    private Long chatId;
    private Integer messageId;
    private String text;
    private String nodeId;
}
