package com.stationerystore.stationery_store.modules.auth.application.dto;

import java.time.Instant;

public record AuthPayload(String accessToken, Instant expiresAt, UserView user) {
}
