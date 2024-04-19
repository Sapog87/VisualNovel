package com.sapog87.visual_novel.app.advice;

import com.sapog87.visual_novel.core.story.validation.ValidationErrorInfo;
import com.sapog87.visual_novel.core.story.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class BaseControllerAdvice {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<List<ValidationErrorInfo>> handleValidationException(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
    }
}
