package com.stationerystore.stationery_store.modules.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.stationerystore.stationery_store.modules.auth.application.dto.CreateUserInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.UpdateUserInput;
import com.stationerystore.stationery_store.modules.auth.domain.Role;
import com.stationerystore.stationery_store.modules.auth.domain.User;
import com.stationerystore.stationery_store.modules.auth.domain.UserRepository;
import com.stationerystore.stationery_store.shared.exception.BusinessException;
import com.stationerystore.stationery_store.shared.exception.ErrorCode;

class UserServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private UserService service;

    private final UUID actorId = UUID.randomUUID();
    private final UUID targetId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new UserService(users, encoder);
        when(users.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createNormalizesUsernameAndHashesPassword() {
        var view = service.create(new CreateUserInput("  Juan.Perez ", "Juan", "", "Clave1234", Role.EMPLOYEE));

        assertThat(view.username()).isEqualTo("juan.perez");
        assertThat(view.email()).isNull();
        assertThat(view.permissions()).isEmpty();
    }

    @Test
    void createRejectsDuplicateUsername() {
        when(users.existsByUsername("juan")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateUserInput("JUAN", "Juan", null, "Clave1234", Role.EMPLOYEE)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getCode())
                .isEqualTo(ErrorCode.CONFLICT);
    }

    @Test
    void cannotDeactivateOwnAccount() {
        when(users.findById(actorId)).thenReturn(Optional.of(admin()));

        assertThatThrownBy(() -> service.setActive(actorId, false, actorId))
                .hasMessageContaining("propia cuenta");
    }

    @Test
    void cannotDeactivateLastActiveAdmin() {
        when(users.findById(targetId)).thenReturn(Optional.of(admin()));
        when(users.countByRoleAndActiveTrue(Role.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> service.setActive(targetId, false, actorId))
                .hasMessageContaining("al menos un administrador");
    }

    @Test
    void cannotDemoteLastActiveAdmin() {
        when(users.findById(targetId)).thenReturn(Optional.of(admin()));
        when(users.countByRoleAndActiveTrue(Role.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> service.update(targetId, new UpdateUserInput("Otro", null, Role.MANAGER), actorId))
                .hasMessageContaining("al menos un administrador");
    }

    @Test
    void canDemoteAdminWhenAnotherActiveAdminExists() {
        when(users.findById(targetId)).thenReturn(Optional.of(admin()));
        when(users.countByRoleAndActiveTrue(Role.ADMIN)).thenReturn(2L);

        var view = service.update(targetId, new UpdateUserInput("Otro", null, Role.MANAGER), actorId);

        assertThat(view.role()).isEqualTo(Role.MANAGER);
    }

    private User admin() {
        return new User("root", "Root", null, encoder.encode("Clave1234"), Role.ADMIN);
    }
}
