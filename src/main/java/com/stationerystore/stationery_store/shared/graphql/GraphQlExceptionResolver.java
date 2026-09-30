package com.stationerystore.stationery_store.shared.graphql;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import com.stationerystore.stationery_store.shared.exception.BusinessException;
import com.stationerystore.stationery_store.shared.exception.ErrorCode;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Translates application exceptions into GraphQL errors with a stable
 * {@code extensions.code}. Security exceptions are handled by Spring's own
 * SecurityDataFetcherExceptionResolver (UNAUTHORIZED / FORBIDDEN).
 */
@Component
public class GraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    private static final Logger log = LoggerFactory.getLogger(GraphQlExceptionResolver.class);

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof BusinessException be) {
            return build(env, errorTypeFor(be.getCode()), be.getCode(), be.getMessage(), Map.of());
        }
        if (ex instanceof ConstraintViolationException cve) {
            Map<String, String> fields = new LinkedHashMap<>();
            for (ConstraintViolation<?> v : cve.getConstraintViolations()) {
                fields.putIfAbsent(leafField(v.getPropertyPath().toString()), v.getMessage());
            }
            String message = fields.values().stream().findFirst().orElse("Datos inválidos");
            return build(env, ErrorType.BAD_REQUEST, ErrorCode.VALIDATION, message, Map.of("fields", fields));
        }
        return null;
    }

    private GraphQLError build(DataFetchingEnvironment env, ErrorType type, ErrorCode code,
            String message, Map<String, Object> extra) {
        Map<String, Object> extensions = new LinkedHashMap<>(extra);
        extensions.put("code", code.name());
        log.debug("GraphQL {} error at {}: {}", code, env.getExecutionStepInfo().getPath(), message);
        return GraphqlErrorBuilder.newError(env)
                .errorType(type)
                .message(message)
                .extensions(extensions)
                .build();
    }

    private static ErrorType errorTypeFor(ErrorCode code) {
        return switch (code) {
            case NOT_FOUND -> ErrorType.NOT_FOUND;
            case INVALID_CREDENTIALS, ACCOUNT_DISABLED -> ErrorType.UNAUTHORIZED;
            case VALIDATION, CONFLICT, BUSINESS_RULE -> ErrorType.BAD_REQUEST;
        };
    }

    /** "createUser.input.username" -> "username" */
    private static String leafField(String path) {
        int idx = path.lastIndexOf('.');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }
}
