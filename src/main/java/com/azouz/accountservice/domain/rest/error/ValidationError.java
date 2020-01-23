package com.azouz.accountservice.domain.rest.error;

public class ValidationError {
    private final String sourceField;
    private final String title;

    public ValidationError(final String sourceField, final String title) {
        this.sourceField = sourceField;
        this.title = title;
    }

    public String getSourceField() {
        return sourceField;
    }

    public String getTitle() {
        return title;
    }
}
