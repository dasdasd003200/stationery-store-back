package com.stationerystore.stationery_store.modules.auth.domain;

/**
 * Fine-grained capabilities. Each new module adds its own permissions here
 * (e.g. {@code inventory:read}) and assigns them to roles in {@link Role}.
 *
 * <p>Use the {@link Codes} constants inside {@code @PreAuthorize} expressions.
 */
public enum Permission {

    USERS_READ(Codes.USERS_READ),
    USERS_WRITE(Codes.USERS_WRITE);

    private final String code;

    Permission(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static final class Codes {
        public static final String USERS_READ = "users:read";
        public static final String USERS_WRITE = "users:write";

        private Codes() {
        }
    }
}
