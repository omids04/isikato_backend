package com.isikato.api.security;

import com.isikato.infrastructure.entities.Token;
import com.isikato.infrastructure.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class IsikatoAuthenticationFilter extends OncePerRequestFilter {

//TODO Change ERROR CODE TO 401
    private final TokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = request.getHeader("Authorization");
        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }
        var username = getUsername(token);
        if (username.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        var authentication = new UsernamePasswordAuthenticationToken(username.get(), token, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private boolean isTokenValid(Token token) {
        return token
                .getExpiration()
                .isAfter(LocalDateTime.now());
    }

    private Optional<String> getUsername(String token) {
        var tokenModel = tokenRepository.findByToken(token);
        if (tokenModel.isEmpty())
            return Optional.empty();
        if(!isTokenValid(tokenModel.get()))
            return Optional.empty();
        String username = tokenModel
                .get()
                .getEmployee()
                .getUsername();
        return Optional.of(username);
    }
}
