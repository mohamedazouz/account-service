package com.azouz.accountservice.domain.rest.error;

import java.util.List;

public class ErrorResponse {
    private final String message;
    private final List<ValidationError> validationsErrors;

    public ErrorResponse(final String message) {
        this.message = message;
        this.validationsErrors = null;
    }

    public ErrorResponse(final String message, final List<ValidationError> validationsErrors) {
        this.message = message;
        this.validationsErrors = validationsErrors;
    }

    public String getMessage() {
        return message;
    }

    public List<ValidationError> getValidationsErrors() {
        return validationsErrors;
    }
}
