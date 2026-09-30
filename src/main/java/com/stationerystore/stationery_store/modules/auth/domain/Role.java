package com.stationerystore.stationery_store.modules.auth.domain;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * A role is a named bundle of permissions. Authorization checks should target
 * permissions; roles exist to make assignment simple for administrators.
 */
public enum Role {

    ADMIN(EnumSet.allOf(Permission.class)),
    MANAGER(EnumSet.of(Permission.USERS_READ)),
    EMPLOYEE(EnumSet.noneOf(Permission.class));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = Collections.unmodifiableSet(permissions);
    }

    public Set<Permission> permissions() {
        return permissions;
    }

    public List<String> permissionCodes() {
        return permissions.stream().map(Permission::code).sorted().toList();
    }

    /** {@code ROLE_<NAME>} plus every permission code, as Spring authorities. */
    public List<GrantedAuthority> authorities() {
        return Stream.concat(
                Stream.of("ROLE_" + name()),
                permissions.stream().map(Permission::code))
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();
    }
}
