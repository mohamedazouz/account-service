package com.azouz.accountservice.middleware;

import com.azouz.accountservice.domain.rest.error.ErrorResponse;
import com.azouz.accountservice.exception.AccountNotFoundException;
import com.azouz.accountservice.exception.BadRequestException;
import com.azouz.accountservice.exception.InsufficientAccountBalanceException;
import com.azouz.accountservice.rest.AccountController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jooby.Context;
import io.jooby.ErrorHandler;
import io.jooby.MediaType;
import io.jooby.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@Singleton
public class ErrorMiddleware implements ErrorHandler {

    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final ObjectMapper objectMapper;

    @Inject
    public ErrorMiddleware(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nonnull
    @Override
    public void apply(@Nonnull final Context ctx, @Nonnull final Throwable cause, @Nonnull final StatusCode statusCode) {
        try {
            String responseBody = this.getResponseBody(new ErrorResponse(ErrorHandler.errorMessage(ctx, statusCode)));
            StatusCode responseStatusCode = statusCode;
            if (cause instanceof BadRequestException) {
                responseStatusCode = StatusCode.BAD_REQUEST;
                responseBody = this.getResponseBody(((BadRequestException) cause).getErrorResponse());
            }
            if (cause instanceof AccountNotFoundException) {
                responseStatusCode = StatusCode.NOT_FOUND;
                responseBody = this.getResponseBody(new ErrorResponse(cause.getMessage()));

            }
            if (cause instanceof InsufficientAccountBalanceException) {
                responseStatusCode = StatusCode.BAD_REQUEST;
                responseBody = this.getResponseBody(new ErrorResponse(cause.getMessage()));
            }
            ctx.setResponseType(MediaType.json)
                    .setResponseCode(responseStatusCode)
                    .send(responseBody);
        } catch (final JsonProcessingException e) {
            log.error("Exception while response on bad request exception", e);
            ctx.setResponseType(MediaType.json)
                    .setResponseCode(StatusCode.SERVER_ERROR);
        }
    }

    private <T> String getResponseBody(final T value) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(value);
    }
}
