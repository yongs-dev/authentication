package com.mark.authentication.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorDetails {

    private final String message;
    private final String details;
    private final LocalDateTime timestamp;
}
