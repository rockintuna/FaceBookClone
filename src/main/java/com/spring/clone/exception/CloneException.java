package com.spring.clone.exception;

import lombok.Getter;

@Getter
public class CloneException extends Exception {

    private final ErrorCode errorCode;

    public CloneException(ErrorCode errorCode) {

        this.errorCode = errorCode;
    }
}

