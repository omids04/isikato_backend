package com.isikato.api.security;

import com.isikato.infrastructure.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IsikatoUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var em = employeeRepository.findByUsername(username);
        if(em.isEmpty())
            throw new UsernameNotFoundException("username " + username + " does not exist!");
        return new IsikatoUserDetails(em.get());
    }
}
