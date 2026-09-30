package com.stationerystore.stationery_store.shared.graphql;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * ISO-8601 UTC timestamp scalar backed by {@link Instant}.
 */
public final class DateTimeScalar {

    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("ISO-8601 timestamp in UTC, e.g. 2026-01-31T13:45:00Z")
            .coercing(new InstantCoercing())
            .build();

    private DateTimeScalar() {
    }

    private static final class InstantCoercing implements Coercing<Instant, String> {

        @Override
        public String serialize(Object value, GraphQLContext context, Locale locale) {
            if (value instanceof Instant instant) return instant.toString();
            if (value instanceof OffsetDateTime odt) return odt.toInstant().toString();
            throw new CoercingSerializeException("Expected Instant but got " + value.getClass().getSimpleName());
        }

        @Override
        public Instant parseValue(Object input, GraphQLContext context, Locale locale) {
            try {
                return Instant.parse(input.toString());
            } catch (DateTimeParseException e) {
                throw new CoercingParseValueException("Invalid DateTime: " + input, e);
            }
        }

        @Override
        public Instant parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext context,
                Locale locale) {
            if (!(input instanceof StringValue sv)) {
                throw new CoercingParseLiteralException("DateTime must be a string");
            }
            try {
                return Instant.parse(sv.getValue());
            } catch (DateTimeParseException e) {
                throw new CoercingParseLiteralException("Invalid DateTime: " + sv.getValue(), e);
            }
        }
    }
}
