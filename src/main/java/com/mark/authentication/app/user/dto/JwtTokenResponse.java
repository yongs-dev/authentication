package com.mark.authentication.app.user.dto;

import lombok.*;

@Getter
@Builder
public class JwtTokenResponse {

    private final String email;
    private final String accessToken;
    private final String refreshToken;
}
