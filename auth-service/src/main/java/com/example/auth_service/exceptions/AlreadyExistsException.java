package com.example.auth_service.exceptions;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {
    private final String messageCode;
    private final Object[] args;


    public AlreadyExistsException(String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
        this.args = null;
    }

}