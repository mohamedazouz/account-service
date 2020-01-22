package com.azouz.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3932580886051016069L;

    public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(final String message) {
        super(message);
    }

    public AccountNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AccountNotFoundException(final Throwable cause) {
        super(cause);
    }
}
