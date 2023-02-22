package com.isikato.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Token;
import com.isikato.infrastructure.repositories.EmployeeRepository;
import com.isikato.infrastructure.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseWebTest {

    @Autowired
    protected TokenRepository tokenRepository;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected WebTestClient client;

    ObjectMapper mapper = new ObjectMapper();


    protected WebTestClient.ResponseSpec sendRq(String body, String path){
    return  client
            .post()
            .uri(path)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
    }

    protected WebTestClient.ResponseSpec sendRq(String body, String token, String path){
        return  client
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .bodyValue(body)
                .exchange();
    }


    protected WebTestClient.ResponseSpec sendRq(Object body, String token, String path, MediaType type){
        return  client
                .post()
                .uri(path)
                .contentType(type)
                .header("Authorization", token)
                .bodyValue(body)
                .exchange();
    }

    protected String toJsonString(Object req) {
        try {
            return mapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    protected String createTokenForTest(){
        var token = tokenRepository.findById(1L);
        if(token.isPresent())
            return token.get().getToken();
        var newToken = Token
                .builder()
                .token("token")
                .employee(Employee.builder().id(1L).build())
                .expiration(LocalDateTime.now().plusDays(1))
                .build();
        tokenRepository.save(newToken);
        return newToken.getToken();
    }

}
