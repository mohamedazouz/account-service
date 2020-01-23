package com.azouz.accountservice.exception;

import com.azouz.accountservice.domain.rest.error.ErrorResponse;

public class BadRequestException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public BadRequestException(final ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
