package com.stationerystore.stationery_store.modules.auth.application;

import java.time.Instant;

import com.stationerystore.stationery_store.modules.auth.domain.User;

/** Port for issuing access tokens; implemented in the infrastructure layer. */
public interface TokenIssuer {

    IssuedToken issue(User user);

    record IssuedToken(String value, Instant expiresAt) {
    }
}
