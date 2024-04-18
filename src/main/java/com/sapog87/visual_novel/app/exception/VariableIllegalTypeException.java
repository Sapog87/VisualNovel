package com.sapog87.visual_novel.app.exception;

public class VariableIllegalTypeException extends RuntimeException {
    public VariableIllegalTypeException() {
        super();
    }

    public VariableIllegalTypeException(String message) {
        super(message);
    }

    public VariableIllegalTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariableIllegalTypeException(Throwable cause) {
        super(cause);
    }
}
