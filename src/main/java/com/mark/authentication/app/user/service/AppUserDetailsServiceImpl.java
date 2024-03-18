package com.mark.authentication.app.user.service;

import com.mark.authentication.app.user.domain.AppUserDetails;
import com.mark.authentication.app.user.repository.UserRepository;
import com.mark.authentication.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsServiceImpl implements AppUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> AppUserDetails.builder()
                        .user(user)
                        .authorities(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue())))
                        .build()
                )
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
