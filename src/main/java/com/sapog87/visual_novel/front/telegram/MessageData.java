package com.sapog87.visual_novel.front.telegram;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageData {
    private Long chatId;
    private Integer messageId;
    private String text;
    private String nodeId;
}
