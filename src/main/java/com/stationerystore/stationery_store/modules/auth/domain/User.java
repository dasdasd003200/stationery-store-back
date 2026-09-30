package com.stationerystore.stationery_store.modules.auth.domain;

import java.time.Instant;
import java.util.Locale;

import com.stationerystore.stationery_store.shared.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    protected User() {
        // for JPA
    }

    public User(String username, String fullName, String email, String passwordHash, Role role) {
        this.username = normalizeUsername(username);
        this.passwordHash = passwordHash;
        updateProfile(fullName, email, role);
    }

    public static String normalizeUsername(String username) {
        return username == null ? null : username.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public void updateProfile(String fullName, String email, Role role) {
        this.fullName = fullName.trim();
        this.email = normalizeEmail(email);
        this.role = role;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void recordLogin(Instant at) {
        this.lastLoginAt = at;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }
}
