package com.stationerystore.stationery_store.modules.auth.application;

import java.time.Clock;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.stationerystore.stationery_store.modules.auth.application.dto.AuthPayload;
import com.stationerystore.stationery_store.modules.auth.application.dto.LoginInput;
import com.stationerystore.stationery_store.modules.auth.application.dto.UserView;
import com.stationerystore.stationery_store.modules.auth.domain.User;
import com.stationerystore.stationery_store.modules.auth.domain.UserRepository;
import com.stationerystore.stationery_store.shared.exception.BusinessException;
import com.stationerystore.stationery_store.shared.exception.ErrorCode;

import jakarta.validation.Valid;

@Service
@Validated
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuer tokenIssuer;
    private final Clock clock;
    /** Compared against when the user does not exist, so response time doesn't leak valid usernames. */
    private final String dummyHash;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, TokenIssuer tokenIssuer, Clock clock) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenIssuer = tokenIssuer;
        this.clock = clock;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing");
    }

    @Transactional
    public AuthPayload login(@Valid LoginInput input) {
        Optional<User> found = users.findByUsername(User.normalizeUsername(input.username()));
        String hash = found.map(User::getPasswordHash).orElse(dummyHash);
        boolean matches = passwordEncoder.matches(input.password(), hash);

        User user = found.filter(u -> matches)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS,
                        "Usuario o contraseña incorrectos"));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED,
                    "Tu cuenta está desactivada. Contacta a un administrador.");
        }

        user.recordLogin(clock.instant());
        TokenIssuer.IssuedToken token = tokenIssuer.issue(user);
        return new AuthPayload(token.value(), token.expiresAt(), UserView.from(user));
    }
}
