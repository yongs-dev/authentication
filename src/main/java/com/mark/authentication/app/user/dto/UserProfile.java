package com.mark.authentication.app.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfile {

    private final String email;
    private final String name;
    private final String provider;
}
