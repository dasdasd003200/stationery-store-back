package com.stationerystore.stationery_store.modules.auth.infrastructure.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.stationerystore.stationery_store.modules.auth.domain.Role;
import com.stationerystore.stationery_store.modules.auth.domain.User;
import com.stationerystore.stationery_store.modules.auth.domain.UserRepository;

/**
 * Creates the first administrator when the users table is empty, so a fresh
 * installation is never locked out.
 */
@Component
@EnableConfigurationProperties(BootstrapAdminProperties.class)
class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapAdminProperties props;

    AdminBootstrap(UserRepository users, PasswordEncoder passwordEncoder, BootstrapAdminProperties props) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!props.enabled() || users.count() > 0) return;

        User admin = new User(props.username(), props.fullName(), null,
                passwordEncoder.encode(props.password()), Role.ADMIN);
        users.save(admin);
        log.warn("Bootstrap admin '{}' created. Change its password after the first login.", admin.getUsername());
    }
}
