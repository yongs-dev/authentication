package com.mark.authentication.app.auth.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class JwtTokenResponse {

    private final String email;
    private final String accessToken;
    private final String refreshToken;
}
