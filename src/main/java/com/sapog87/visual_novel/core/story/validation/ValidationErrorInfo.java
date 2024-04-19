package com.sapog87.visual_novel.core.story.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationErrorInfo {
    private String id;
    private String message;
}
