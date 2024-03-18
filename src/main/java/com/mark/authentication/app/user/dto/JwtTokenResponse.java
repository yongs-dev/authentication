package com.mark.authentication.app.user.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JwtTokenResponse {

    private String email;
    private String accessToken;
    private String refreshToken;
}
