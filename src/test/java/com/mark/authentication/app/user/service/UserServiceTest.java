package com.mark.authentication.app.user.service;

import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.dto.SignUpRequest;
import com.mark.authentication.app.user.repository.UserRepository;
import com.mark.authentication.enums.role.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String EMAIL = "yongseok993@gmail.com";
    private static final String PASSWORD = "Dydtjr@12";

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void signUp() {
        // given
        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        final SignUpRequest signUpRequest = signUpRequest();
        final String encryptedPassword = encoder.encode(signUpRequest.getPassword());

        // when
        doReturn(User.builder().email(signUpRequest.getEmail()).password(encryptedPassword).role(UserRole.ROLE_USER).build())
                .when(userRepository)
                .save(any(User.class));

        final User user = userService.signUp(signUpRequest);

        // then
        assertThat(user.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(encoder.matches(signUpRequest.getPassword(), user.getPassword())).isTrue();

        // verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
    }

    @Test
    public void isEmailDuplicated() {
        // given
        final SignUpRequest signUpRequest = signUpRequest();
        doReturn(true).when(userRepository).existsByEmail(signUpRequest.getEmail());

        // when
        final boolean isDuplicated = userService.isEmailDuplicated(signUpRequest.getEmail());

        // then
        assertThat(isDuplicated).isTrue();
    }

    @Test
    public void findAll() {
        // given
        doReturn(userList()).when(userRepository).findAll();

        // when
        final List<User> userList = userService.findAll();

        // then
        assertThat(userList.size()).isEqualTo(5);
    }

    private SignUpRequest signUpRequest() {
        return SignUpRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    private List<User> userList() {
        final List<User> userList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            userList.add(User.builder().email(EMAIL).password(PASSWORD).build());
        }
        return userList;
    }
}
