package com.mark.authentication.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mark.authentication.exception.ErrorDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j(topic = "FORBIDDEN_EXCEPTION_HANDLER")
@RequiredArgsConstructor
@Component
public class ApplicationAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("Not Authorities", accessDeniedException);

        final ErrorDetails errorDetails = ErrorDetails.builder()
                .message(accessDeniedException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
