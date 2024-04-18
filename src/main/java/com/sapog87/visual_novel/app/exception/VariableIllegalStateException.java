package com.sapog87.visual_novel.app.exception;

public class VariableIllegalStateException extends RuntimeException {
    public VariableIllegalStateException() {
        super();
    }

    public VariableIllegalStateException(String message) {
        super(message);
    }

    public VariableIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariableIllegalStateException(Throwable cause) {
        super(cause);
    }
}
