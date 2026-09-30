package com.stationerystore.stationery_store.modules.auth.infrastructure.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth.bootstrap-admin")
public record BootstrapAdminProperties(boolean enabled, String username, String password, String fullName) {
}
