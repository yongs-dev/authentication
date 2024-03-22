package com.mark.authentication.config.handler;

import com.mark.authentication.app.user.domain.User;
import com.mark.authentication.app.user.dto.JwtTokenResponse;
import com.mark.authentication.enums.role.UserRole;
import com.mark.authentication.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        final JwtTokenResponse jwtTokenResponse = jwtTokenProvider.generateJwtToken(authenticationToEntity(authentication));
        log.info("OAuth2 Login Success. accessToken: {}, refreshToken: {}", jwtTokenResponse.getAccessToken(), jwtTokenResponse.getRefreshToken());
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:8088/index.html");
    }

    private User authenticationToEntity(final Authentication authentication) {
        final OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        final String email = oAuth2User.getAttribute("email");
        final String provider = oAuth2User.getAttribute("provider");
        final String role = oAuth2User.getAuthorities().stream()
                .findFirst()
                .orElseThrow(IllegalAccessError::new)
                .getAuthority();

        return User.builder()
                .email(email)
                .role(UserRole.valueOf(role))
                .provider(provider)
                .build();
    }
}
