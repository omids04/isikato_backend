package com.isikato.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.infrastructure.repositories.EmployeeRepository;
import com.isikato.infrastructure.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper mapper;
    private final TokenRepository tokenRepository;
    private final EmployeeRepository employeeRepository;



    @Bean
    public PasswordEncoder passwordEncoder(){
        var bcrypt = new BCryptPasswordEncoder();
        var encoders = Map.<String, PasswordEncoder>of("bcrypt", bcrypt);
        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .addFilter(this.createLoginFilter())
                .addFilterAfter(isikatoAuthenticationFilter(), IsikatoLoginFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().mvcMatchers("/employees/getInfo").authenticated()
                .and()
                .authorizeRequests().mvcMatchers("/employees/authenticate").permitAll()
                .and()
                .authorizeRequests().mvcMatchers("/categories/getAll").permitAll()
                .and()
                .authorizeRequests().mvcMatchers("/categories/getInfo").permitAll()
                .and()
                .authorizeRequests().mvcMatchers("/contents/getAll").permitAll()
                .and()
                .authorizeRequests().mvcMatchers("/banner/getAll").permitAll()
                .and()
                .authorizeRequests().mvcMatchers("/contents/getInfo").permitAll()
                .and()
                .authorizeRequests().mvcMatchers("/contents/visits").permitAll()
                .and()
                .authorizeRequests().mvcMatchers("/files/*/*").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }


    private UsernamePasswordAuthenticationFilter createLoginFilter() throws Exception {
        var filter = new IsikatoLoginFilter(mapper, authenticationManager(), tokenRepository, employeeRepository);
        filter.setFilterProcessesUrl("/employees/authenticate");
        filter.setAuthenticationFailureHandler(new IsikatoAuthenticationFailureHandler(mapper));
        return filter;
    }

    private IsikatoAuthenticationFilter isikatoAuthenticationFilter(){
        return new IsikatoAuthenticationFilter(tokenRepository);
    }
}
