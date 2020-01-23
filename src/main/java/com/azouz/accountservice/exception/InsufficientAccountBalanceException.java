package com.azouz.accountservice.exception;

public class InsufficientAccountBalanceException extends RuntimeException {

    public InsufficientAccountBalanceException(final String message) {
        super(message);
    }
}
