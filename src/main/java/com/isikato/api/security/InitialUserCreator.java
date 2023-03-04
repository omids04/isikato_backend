package com.isikato.api.security;

import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialUserCreator implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        var pass = "isikato";
        var username = "isikato";

        var encryptedPass = passwordEncoder.encode(pass);

        var user = employeeRepository.findByUsername(username);
        if(user.isPresent())
            return;
        var admin = Employee
                .builder()
                .password(encryptedPass)
                .username(username)
                .enabled(true)
                .build();
        employeeRepository.save(admin);
        log.info("Admin User with Username {} and Password {} Created", username, pass);
    }
}
