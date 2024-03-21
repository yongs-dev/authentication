package com.mark.authentication.app.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpRequest {

    private final String email;

    @JsonProperty("user_name")
    private final String userName;

    private final String password;
}
