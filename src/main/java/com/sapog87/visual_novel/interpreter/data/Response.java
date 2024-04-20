package com.sapog87.visual_novel.interpreter.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private Long userId;
    private String answer;
    private Boolean stop;

    @Override
    public String toString() {
        return "Response{" +
                "userId=" + userId +
                ", answer='" + answer + '\'' +
                ", stop=" + stop +
                '}';
    }
}
