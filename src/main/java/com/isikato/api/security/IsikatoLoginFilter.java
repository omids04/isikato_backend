package com.isikato.api.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.api.model.req.LoginModel;
import com.isikato.api.model.res.AuthenticatedUserModel;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Permission;
import com.isikato.infrastructure.entities.Token;
import com.isikato.infrastructure.repositories.EmployeeRepository;
import com.isikato.infrastructure.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class IsikatoLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final EmployeeRepository employeeRepository;
    private final Duration validityDuration = Duration.ofDays(1);


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            var loginModel = mapper.readValue(request.getInputStream(), LoginModel.class);
            var token = new UsernamePasswordAuthenticationToken(loginModel.getUsername(), loginModel.getPassword());
            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new BadCredentialsException("problem in reading username password");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var token = getToken(authResult);
        var em = token.getEmployee();
        var nodes = em
                .getPermissions()
                .stream()
                .map(Permission::getPermissions)
                .map(s -> {
                    try {
                        return mapper.readTree(s);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        var authenticatedUserModel = AuthenticatedUserModel
                .builder()
                .token(token.getToken())
                .expiration(token.getExpiration())
                .permissions(nodes)
                .build();
        response.getWriter().write(mapper.writeValueAsString(authenticatedUserModel));
    }

    private Token getToken(Authentication authResult) {
        var username = ((UserDetails)authResult.getPrincipal()).getUsername();
        var user = employeeRepository.findByUsername(username).orElse(Employee.builder().build());
        if(user.getToken()!= null && user.getToken().getExpiration().isAfter(LocalDateTime.now()))
            return renewToken(user.getToken());
        return createToken(user);
    }

    private Token createToken(Employee user) {
        var tokenString = UUID.randomUUID().toString();
        var token = Token
                .builder()
                .token(tokenString)
                .expiration(LocalDateTime.now().plus(validityDuration))
                .employee(user)
                .build();
        return tokenRepository.save(token);
    }

    private Token renewToken(Token token) {
        token.setExpiration(LocalDateTime.now().plus(validityDuration));
        tokenRepository.save(token);
        return token;
    }
}
