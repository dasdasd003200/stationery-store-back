package com.stationerystore.stationery_store.shared.exception;

/**
 * Stable, client-facing error codes. The frontend switches on these values,
 * so never rename an existing one.
 */
public enum ErrorCode {
    VALIDATION,
    NOT_FOUND,
    CONFLICT,
    BUSINESS_RULE,
    INVALID_CREDENTIALS,
    ACCOUNT_DISABLED
}
