package com.mark.authentication.app.user.service;

import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.dto.SignUpRequest;
import com.mark.authentication.app.user.repository.UserRepository;
import com.mark.authentication.enums.role.UserRole;
import com.mark.authentication.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(final SignUpRequest signUpRequest) {
        return userRepository.save(
                User.builder()
                        .email(signUpRequest.getEmail())
                        .name(signUpRequest.getName())
                        .password(passwordEncoder.encode(signUpRequest.getPassword()))
                        .role(UserRole.ROLE_USER)
                        .build()
        );
    }

    public boolean isEmailDuplicated(final String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmailAndProvider(final String email, final String provider) {
        return userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
