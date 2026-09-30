package com.stationerystore.stationery_store.modules.auth.api.graphql;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.stationerystore.stationery_store.modules.auth.application.UserService;
import com.stationerystore.stationery_store.modules.auth.application.dto.CreateUserInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.ResetPasswordInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.UpdateUserInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.UserFilter;
import com.stationerystore.stationery_store.modules.auth.application.dto.UserView;
import com.stationerystore.stationery_store.modules.auth.domain.Permission;
import com.stationerystore.stationery_store.shared.pagination.PageInput;
import com.stationerystore.stationery_store.shared.pagination.PageResult;

@Controller
class UserController {

    private static final String CAN_READ = "hasAuthority('" + Permission.Codes.USERS_READ + "')";
    private static final String CAN_WRITE = "hasAuthority('" + Permission.Codes.USERS_WRITE + "')";

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    @PreAuthorize(CAN_READ)
    PageResult<UserView> users(@Argument UserFilter filter, @Argument PageInput page) {
        return userService.search(filter, page);
    }

    @QueryMapping
    @PreAuthorize(CAN_READ)
    UserView user(@Argument UUID id) {
        return userService.findById(id);
    }

    @MutationMapping
    @PreAuthorize(CAN_WRITE)
    UserView createUser(@Argument CreateUserInput input) {
        return userService.create(input);
    }

    @MutationMapping
    @PreAuthorize(CAN_WRITE)
    UserView updateUser(@Argument UUID id, @Argument UpdateUserInput input,
            @AuthenticationPrincipal Jwt jwt) {
        return userService.update(id, input, actorId(jwt));
    }

    @MutationMapping
    @PreAuthorize(CAN_WRITE)
    UserView setUserActive(@Argument UUID id, @Argument boolean active, @AuthenticationPrincipal Jwt jwt) {
        return userService.setActive(id, active, actorId(jwt));
    }

    @MutationMapping
    @PreAuthorize(CAN_WRITE)
    boolean resetUserPassword(@Argument UUID id, @Argument ResetPasswordInput input) {
        userService.resetPassword(id, input);
        return true;
    }

    private static UUID actorId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
