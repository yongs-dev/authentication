package com.mark.authentication.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorDetails {

    private String message;
    private String details;
    private LocalDateTime timestamp;
}
