package com.azouz.accountservice.exception;

public class InsufficientAccountBalanceException extends RuntimeException {

    private static final long serialVersionUID = -3932580886051016069L;

    public InsufficientAccountBalanceException() {
        super();
    }

    public InsufficientAccountBalanceException(final String message) {
        super(message);
    }

    public InsufficientAccountBalanceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InsufficientAccountBalanceException(final Throwable cause) {
        super(cause);
    }
}
