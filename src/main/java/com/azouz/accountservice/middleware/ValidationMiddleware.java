package com.azouz.accountservice.middleware;

import com.azouz.accountservice.domain.rest.error.ErrorResponse;
import com.azouz.accountservice.domain.rest.error.ValidationError;
import com.azouz.accountservice.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jooby.Context;
import io.jooby.MessageDecoder;

import javax.annotation.Nonnull;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

@Singleton
public class ValidationMiddleware implements MessageDecoder {

    private final ObjectMapper objectMapper;

    @Inject
    public ValidationMiddleware(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nonnull
    @Override
    public <T> T decode(@Nonnull final Context ctx, @Nonnull final Type type) throws Exception {
        final TypeFactory typeFactory = objectMapper.getTypeFactory();
        final byte[] body = ctx.body().bytes();
        final T value = objectMapper.readValue(body, typeFactory.constructType(type));
        final List<ValidationError> errors = validate(value);
        if (errors.size() > 0) {
            throw new BadRequestException(new ErrorResponse("Invalid data", errors));
        }
        return value;
    }

    public <T> List<ValidationError> validate(final T value) {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        final Set<ConstraintViolation<T>> violations = validator.validate(value);
        final List<ValidationError> errors = Lists.newArrayList();
        violations.forEach(violation ->
                errors.add(new ValidationError(violation.getPropertyPath().toString(), violation.getMessage())));
        return errors;
    }
}
