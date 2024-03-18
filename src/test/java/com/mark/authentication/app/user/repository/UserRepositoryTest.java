package com.mark.authentication.app.user.repository;

import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.config.properties.JasyptConfig;
import com.mark.authentication.enums.role.UserRole;
import com.mark.authentication.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JasyptConfig.class)
class UserRepositoryTest {

    private static final String EMAIL = "yongseok993@gmail.com";
    private static final String PASSWORD = "Dydtjr@12";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        final User user = User.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);
    }

    @Test
    void findByEmailAndPassword() {
        final User user = userRepository.findByEmailAndPassword(EMAIL, PASSWORD);

        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void findByEmail() {
        final User user = userRepository.findByEmail(EMAIL)
                .orElseThrow(() -> new UserNotFoundException(EMAIL));

        assertThat(user.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void existsByEmail() {
        assertThat(userRepository.existsByEmail(EMAIL)).isTrue();
    }
}