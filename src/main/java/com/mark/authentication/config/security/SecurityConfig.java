package com.mark.authentication.config.security;

import com.mark.authentication.app.user.service.AppUserDetailsService;
import com.mark.authentication.app.user.service.OAuthUserService;
import com.mark.authentication.config.filter.AuthFilter;
import com.mark.authentication.config.handler.ApplicationAccessDeniedHandler;
import com.mark.authentication.config.handler.ApplicationAuthenticationEntryPoint;
import com.mark.authentication.config.handler.OAuth2AuthenticationSuccessHandler;
import com.mark.authentication.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] AUTH_WHITE_LIST = {
            "/favicon.ico", "/index.html", "/users/signUp", "/login/oauth2/**"
    };

    private final AppUserDetailsService appUserDetailsService;
    private final OAuthUserService oAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationAccessDeniedHandler applicationAccessDeniedHandler;
    private final ApplicationAuthenticationEntryPoint applicationAuthenticationEntryPoint;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain configurer(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    auth.requestMatchers(AUTH_WHITE_LIST).permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new AuthFilter(appUserDetailsService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandler -> exceptionHandler
                        .accessDeniedHandler(applicationAccessDeniedHandler)
                        .authenticationEntryPoint(applicationAuthenticationEntryPoint)
                )
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2UserService))
                )
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*")
                        .allowedOrigins("http://localhost:3000", "http://localhost:3001");
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
