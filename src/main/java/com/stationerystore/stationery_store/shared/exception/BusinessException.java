package com.stationerystore.stationery_store.shared.exception;

/**
 * Expected failure of a use case. The message is safe to show to end users.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode code;

    public BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.NOT_FOUND, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(ErrorCode.CONFLICT, message);
    }

    public static BusinessException rule(String message) {
        return new BusinessException(ErrorCode.BUSINESS_RULE, message);
    }
}
