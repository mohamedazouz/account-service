package com.azouz.accountservice.middleware;

import com.azouz.accountservice.domain.rest.error.ErrorResponse;
import com.azouz.accountservice.exception.BadRequestException;
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
        if (cause instanceof BadRequestException) {
            this.handleBadRequestException(ctx, (BadRequestException) cause);
        }
        this.log.error("uncathed error", cause);
    }

    public void handleBadRequestException(@Nonnull final Context ctx,
                                          @Nonnull final BadRequestException badRequestException) {
        final ErrorResponse errorResponse = badRequestException.getErrorResponse();
        try {
            ctx.setResponseType(MediaType.json)
                    .setResponseCode(StatusCode.BAD_REQUEST)
                    .send(new ObjectMapper().writeValueAsString(errorResponse));
        } catch (final JsonProcessingException e) {
            log.error("Exception while response on badrequest exception", e);
            ctx.setResponseType(MediaType.json)
                    .setResponseCode(StatusCode.SERVER_ERROR);
        }
    }
}
