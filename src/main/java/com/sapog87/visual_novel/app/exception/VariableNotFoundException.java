package com.sapog87.visual_novel.app.exception;

public class VariableNotFoundException extends RuntimeException {
    public VariableNotFoundException() {
        super();
    }

    public VariableNotFoundException(String message) {
        super(message);
    }

    public VariableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariableNotFoundException(Throwable cause) {
        super(cause);
    }
}
