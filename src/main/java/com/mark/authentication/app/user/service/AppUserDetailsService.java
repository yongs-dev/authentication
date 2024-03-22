package com.mark.authentication.app.user.service;

import com.mark.authentication.app.user.domain.AppUserInfos;
import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.repository.UserRepository;
import com.mark.authentication.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String info) throws UsernameNotFoundException {
        final String[] infos = info.split("\\|");

        return userRepository.findByEmailAndProvider(infos[0], infos[1])
                .map(user -> AppUserInfos.builder()
                        .user(user)
                        .authorities(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue())))
                        .attributes(getUserAttributes(user))
                        .build()
                )
                .orElseThrow(() -> new UserNotFoundException(infos[0]));
    }

    private Map<String, Object> getUserAttributes(final User user) {
        return new HashMap() {{
            put("email", user.getEmail());
            put("name", user.getName());
            put("provider", user.getProvider());
        }};
    }
}
