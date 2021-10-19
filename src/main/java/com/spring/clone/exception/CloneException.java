package com.spring.clone.exception;

import lombok.Getter;

@Getter
public class CloneException extends RuntimeException {

    private final ErrorCode errorCode;

    public CloneException(ErrorCode errorCode) {

        this.errorCode = errorCode;
    }
}

