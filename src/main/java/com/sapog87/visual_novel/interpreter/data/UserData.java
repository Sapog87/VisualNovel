package com.sapog87.visual_novel.interpreter.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserData extends Data {
    private Long userId;
    private String storyId;
    private String userMessage;

    public UserData(Long userId, String storyId, String text) {
        this.userId = userId;
        this.storyId = storyId;
        this.userMessage = text;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "userId=" + userId +
                ", message='" + userMessage + '\'' +
                '}';
    }
}
