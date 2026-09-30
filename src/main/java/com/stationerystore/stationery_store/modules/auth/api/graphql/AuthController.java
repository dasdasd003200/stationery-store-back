package com.stationerystore.stationery_store.modules.auth.api.graphql;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.stationerystore.stationery_store.modules.auth.application.AuthService;
import com.stationerystore.stationery_store.modules.auth.application.UserService;
import com.stationerystore.stationery_store.modules.auth.application.dto.AuthPayload;
import com.stationerystore.stationery_store.modules.auth.application.dto.ChangePasswordInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.LoginInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.UserView;

@Controller
class AuthController {

    private final AuthService authService;
    private final UserService userService;

    AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @MutationMapping
    AuthPayload login(@Argument LoginInput input) {
        return authService.login(input);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    UserView me(@AuthenticationPrincipal Jwt jwt) {
        return userService.findById(UUID.fromString(jwt.getSubject()));
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    boolean changeMyPassword(@Argument ChangePasswordInput input, @AuthenticationPrincipal Jwt jwt) {
        userService.changeOwnPassword(UUID.fromString(jwt.getSubject()), input);
        return true;
    }
}
