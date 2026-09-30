package com.stationerystore.stationery_store.modules.auth.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, Duration expiration, String issuer) {

    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("app.security.jwt.secret must be at least 32 bytes (HS256)");
        }
        if (expiration == null) expiration = Duration.ofHours(8);
        if (issuer == null || issuer.isBlank()) issuer = "stationery-store";
    }
}
