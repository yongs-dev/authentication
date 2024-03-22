package com.mark.authentication.app.user.service;

import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.dto.SignUpRequest;

import java.util.List;

public interface UserService {

    User signUp(final SignUpRequest signUpRequest);

    boolean isEmailDuplicated(final String email);

    List<User> findAll();

    User findByEmailAndProvider(final String email, final String provider);
}
