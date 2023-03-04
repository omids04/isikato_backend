package com.isikato.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.api.model.req.LoginModel;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Token;
import com.isikato.infrastructure.repositories.EmployeeRepository;
import com.isikato.infrastructure.repositories.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.core.IsNot.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IsikatoLoginFilterTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserDetailsService userDetailsService;

    @MockBean
    TokenRepository tokenRepository;

    @MockBean
    EmployeeRepository employeeRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    String authPath = "/employees/authenticate";

    @Test
    void whenForAUserAnExpiredTokenExistThenANewOneShouldGetsCreated() throws Exception {
        //given
        var username = "user";
        var userPass = "pass";
        var tokenString = UUID.randomUUID().toString();
        var userDetails = User.withUsername(username).password(userPass).authorities("admin").build();
        var token = Token.builder().token(tokenString).expiration(LocalDateTime.now().minusHours(5)).build();
        var user = Employee.builder().username(username).token(token).build();
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(userPass, userPass)).thenReturn(true);
        when(tokenRepository.save(any())).thenAnswer(i -> Token.builder().token(UUID.randomUUID().toString()).creationTime(LocalDateTime.now()).build());
        var reqBody = asJsonString(LoginModel.builder().username(username).password(userPass).build());
        mockMvc.perform(
                        post(authPath)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(not(tokenString)));
    }
    @Test
    void whenForAUserANonExpiredTokenExistThenItShouldGetsRenewed() throws Exception {
        //given
        var username = "user";
        var userPass = "pass";
        var tokenString = UUID.randomUUID().toString();
        var userDetails = User.withUsername(username).password(userPass).authorities("admin").build();
        var token = Token.builder().token(tokenString).expiration(LocalDateTime.now().plusHours(5)).build();
        var user = Employee.builder().username(username).token(token).build();
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(userPass, userPass)).thenReturn(true);
        when(tokenRepository.save(any())).thenAnswer(i -> Token.builder().token(UUID.randomUUID().toString()).creationTime(LocalDateTime.now()).build());
        var reqBody = asJsonString(LoginModel.builder().username(username).password(userPass).build());
        mockMvc.perform(
                        post(authPath)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenString));
    }

    @Test
    void tryingToAuthenticateWithNonExistingUsername() throws Exception {
        //given
        var username = "user";
        var userPass = "pass";
        when(userDetailsService.loadUserByUsername(username)).thenThrow(UsernameNotFoundException.class);
        var reqBody = asJsonString(LoginModel.builder().username(username).password(userPass).build());

        //when
        //then
        mockMvc.perform(
                        post(authPath)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path").value(authPath))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    void tryingToAuthenticateWithInCorrectPassword() throws Exception {
        //given
        var username = "user";
        var userPass = "pass";
        var incorrectPass = "pAss";
        var userDetails = User.withUsername(username).password(userPass).authorities("admin").build();
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(incorrectPass, userPass)).thenReturn(false);
        var reqBody = asJsonString(LoginModel.builder().username(username).password(incorrectPass).build());

        //when
        //then
        mockMvc.perform(
                        post(authPath)
                                .content(reqBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.path").value(authPath))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void tryingToAuthenticateWithCorrectCredentials() throws Exception {
        //given
        var username = "user";
        var userPass = "pass";
        var token = UUID.randomUUID().toString();
        var userDetails = User.withUsername(username).password(userPass).authorities("admin").build();
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(userPass, userPass)).thenReturn(true);
        when(tokenRepository.save(any())).thenReturn(Token.builder().token(token).expiration(LocalDateTime.now().plusDays(1)).creationTime(LocalDateTime.now()).build());
        var reqBody = asJsonString(LoginModel.builder().username(username).password(userPass).build());

        //when
        //then
        mockMvc.perform(
                post(authPath)
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.expiration").exists());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}