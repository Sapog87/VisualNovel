package com.sapog87.visual_novel.core.story;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private final List<ValidationErrorInfo> errors;

    public ValidationException(List<ValidationErrorInfo> errors) {this.errors = errors;}

    @Override
    public String toString() {
        return "ValidationException{" +
                "errors=" + errors +
                '}';
    }
}
