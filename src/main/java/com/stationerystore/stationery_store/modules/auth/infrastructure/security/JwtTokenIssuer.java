package com.stationerystore.stationery_store.modules.auth.infrastructure.security;

import java.time.Clock;
import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.stationerystore.stationery_store.modules.auth.application.TokenIssuer;
import com.stationerystore.stationery_store.modules.auth.domain.User;

@Component
class JwtTokenIssuer implements TokenIssuer {

    private final JwtEncoder encoder;
    private final JwtProperties props;
    private final Clock clock;

    JwtTokenIssuer(JwtEncoder encoder, JwtProperties props, Clock clock) {
        this.encoder = encoder;
        this.props = props;
        this.clock = clock;
    }

    @Override
    public IssuedToken issue(User user) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(props.expiration());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.issuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedToken(value, expiresAt);
    }
}
