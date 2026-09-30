package com.stationerystore.stationery_store.modules.auth.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.stationerystore.stationery_store.modules.auth.application.dto.ChangePasswordInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.CreateUserInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.ResetPasswordInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.UpdateUserInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.UserFilter;
import com.stationerystore.stationery_store.modules.auth.application.dto.UserView;
import com.stationerystore.stationery_store.modules.auth.domain.Role;
import com.stationerystore.stationery_store.modules.auth.domain.User;
import com.stationerystore.stationery_store.modules.auth.domain.UserRepository;
import com.stationerystore.stationery_store.modules.auth.domain.UserSpecifications;
import com.stationerystore.stationery_store.shared.exception.BusinessException;
import com.stationerystore.stationery_store.shared.pagination.PageInput;
import com.stationerystore.stationery_store.shared.pagination.PageResult;

import jakarta.validation.Valid;

@Service
@Validated
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    private static final Sort DEFAULT_SORT = Sort.by("fullName").ascending().and(Sort.by("username"));

    public PageResult<UserView> search(@Valid UserFilter filter, @Valid PageInput page) {
        UserFilter f = filter != null ? filter : UserFilter.NONE;
        Pageable pageable = PageInput.orDefault(page).toPageable(DEFAULT_SORT);
        return PageResult.from(
                users.findAll(UserSpecifications.matching(f.search(), f.role(), f.active()), pageable),
                UserView::from);
    }

    public UserView findById(UUID id) {
        return UserView.from(getOrThrow(id));
    }

    @Transactional
    public UserView create(@Valid CreateUserInput input) {
        String username = User.normalizeUsername(input.username());
        if (users.existsByUsername(username)) {
            throw BusinessException.conflict("El usuario '" + username + "' ya existe");
        }
        String email = User.normalizeEmail(input.email());
        if (email != null && users.existsByEmail(email)) {
            throw BusinessException.conflict("El correo ya está registrado");
        }
        User user = new User(username, input.fullName(), email,
                passwordEncoder.encode(input.password()), input.role());
        return toView(user);
    }

    @Transactional
    public UserView update(UUID id, @Valid UpdateUserInput input, UUID actorId) {
        User user = getOrThrow(id);
        String email = User.normalizeEmail(input.email());
        if (email != null && users.existsByEmailAndIdNot(email, id)) {
            throw BusinessException.conflict("El correo ya está registrado");
        }
        if (user.isAdmin() && input.role() != Role.ADMIN) {
            if (id.equals(actorId)) {
                throw BusinessException.rule("No puedes quitarte el rol de administrador a ti mismo");
            }
            ensureAnotherActiveAdminExists(user);
        }
        user.updateProfile(input.fullName(), email, input.role());
        return toView(user);
    }

    @Transactional
    public UserView setActive(UUID id, boolean active, UUID actorId) {
        User user = getOrThrow(id);
        if (active) {
            user.activate();
        } else {
            if (id.equals(actorId)) {
                throw BusinessException.rule("No puedes desactivar tu propia cuenta");
            }
            if (user.isAdmin()) ensureAnotherActiveAdminExists(user);
            user.deactivate();
        }
        return toView(user);
    }

    @Transactional
    public void resetPassword(UUID id, @Valid ResetPasswordInput input) {
        getOrThrow(id).changePassword(passwordEncoder.encode(input.newPassword()));
    }

    @Transactional
    public void changeOwnPassword(UUID userId, @Valid ChangePasswordInput input) {
        User user = getOrThrow(userId);
        if (!passwordEncoder.matches(input.currentPassword(), user.getPasswordHash())) {
            throw BusinessException.rule("La contraseña actual no es correcta");
        }
        user.changePassword(passwordEncoder.encode(input.newPassword()));
    }

    private void ensureAnotherActiveAdminExists(User admin) {
        long activeAdmins = users.countByRoleAndActiveTrue(Role.ADMIN);
        if (admin.isActive() && activeAdmins <= 1) {
            throw BusinessException.rule("Debe existir al menos un administrador activo");
        }
    }

    /**
     * Audit timestamps are generated by Hibernate when the INSERT/UPDATE runs,
     * so flush before building the response to return the real values.
     */
    private UserView toView(User user) {
        return UserView.from(users.saveAndFlush(user));
    }

    private User getOrThrow(UUID id) {
        return users.findById(id).orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));
    }
}
