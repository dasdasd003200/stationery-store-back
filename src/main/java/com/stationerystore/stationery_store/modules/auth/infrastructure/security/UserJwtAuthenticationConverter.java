package com.stationerystore.stationery_store.modules.auth.infrastructure.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import com.stationerystore.stationery_store.modules.auth.domain.User;
import com.stationerystore.stationery_store.modules.auth.domain.UserRepository;

/**
 * Resolves authorities from the <em>current</em> database state instead of the
 * token claims, so deactivations and role changes take effect immediately.
 */
@Component
class UserJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository users;

    UserJwtAuthenticationConverter(UserRepository users) {
        this.users = users;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        User user = parseId(jwt.getSubject())
                .flatMap(users::findById)
                .filter(User::isActive)
                .orElseThrow(() -> new InvalidBearerTokenException("User is inactive or no longer exists"));
        return new JwtAuthenticationToken(jwt, user.getRole().authorities(), jwt.getSubject());
    }

    private static Optional<UUID> parseId(String subject) {
        try {
            return Optional.of(UUID.fromString(subject));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
