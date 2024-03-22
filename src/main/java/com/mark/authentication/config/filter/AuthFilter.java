package com.mark.authentication.config.filter;

import com.mark.authentication.app.user.service.AppUserDetailsService;
import com.mark.authentication.constants.AuthConstants;
import com.mark.authentication.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(AuthConstants.AUTH_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith(AuthConstants.TOKEN_TYPE)) {
            final String token = authorizationHeader.substring(AuthConstants.TOKEN_TYPE.length());

            if (jwtTokenProvider.isValidToken(token)) {
                final String email = jwtTokenProvider.getEmailFromToken(token);
                final String provider = jwtTokenProvider.getProviderFromToken(token);

                final UserDetails userDetails = appUserDetailsService.loadUserByUsername(email + "|" + provider);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}