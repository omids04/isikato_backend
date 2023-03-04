package com.isikato.api.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.api.model.res.IsikatoErrorModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class IsikatoAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        var path = request.getRequestURI();
        var msg = exception.getMessage();
        var timestamp = LocalDateTime.now();
        var modelString = createErrorModelString(path, msg, timestamp);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(modelString);
    }

    private String createErrorModelString(String path, String msg, LocalDateTime timestamp) throws JsonProcessingException {
        var model = IsikatoErrorModel
                .builder()
                .message(msg)
                .path(path)
                .timestamp(timestamp)
                .build();
        return mapper.writeValueAsString(model);
    }
}
