package com.isikato.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.UserLoginAudit;
import com.isikato.infrastructure.repositories.*;
import com.isikato.service.dtos.CollectionWithCount;
import com.isikato.service.exceptions.EmployeeException;

import java.util.List;
import java.util.stream.Collectors;

import com.isikato.service.specs.EmployeeSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileRepository imageRepository;
    private final UserLoginAuditRepository auditRepository;
   
    @Transactional
    public Employee createOrUpdate(Employee em){
        checkIfUsernameIsValid(em);

        var id = em.getId();

        if(em.getImage().getId() > 0) {
            var img = imageRepository.findById(em.getImage().getId());
            if (img.isPresent())
                em.setImage(img.get());
            else
                throw new EmployeeException("image with id %s does not exist".formatted(em.getImage().getId()));
        }

        //means we should update employee
        if(id != 0){
            var passIsNull = em.getPassword() == null;
            var updated = 0L;

            if(passIsNull)
            {
                updated = employeeRepository.update(em);
            }
            else
            {
                checkAndEncodePassword(em);
                updated = employeeRepository.updateWithPassword(em);
            }
            if(updated == 0)
                throw new EmployeeException("Employee With Id %s Does Not Exist".formatted(id));
            return em;
        }

        checkAndEncodePassword(em);
        return employeeRepository.save(em);
    }


    private void checkAndEncodePassword(Employee em) {
        var isNull = em.getPassword() == null;
        if(isNull)
        {
            throw new EmployeeException("Password Can't Be Null");
        }

        var isLongEnough = em.getPassword().trim().length() >= 8;
        if(!isLongEnough)
        {
            throw new EmployeeException("Password Should Have More Than 7 Characters");
        }

        var encoded = passwordEncoder.encode(em.getPassword());
        em.setPassword(encoded);
    }

    private void checkIfUsernameIsValid(Employee em) {
        var isNull = em.getUsername() == null;
        if(isNull)
        {
            throw new EmployeeException("Username Can't Be Null");
        }

        em.setUsername(em.getUsername().trim());
        var isLongEnough = em.getUsername().length() >= 4;
        if(!isLongEnough)
        {
            throw new EmployeeException("Username Should Have More Than 3 Characters");
        }
    }

    @Transactional
    public boolean remove(long id){
        var count = employeeRepository.removeById(id);
        return count == 1L;
    }

    public Employee get(String username) {
        var em = employeeRepository.findByUsername(username);
        if (em.isEmpty())
            throw new EmployeeException("employee with username %s does not exist".formatted(username));
        return em.get();
    }

    public CollectionWithCount<Employee> getAll(PageRequest pageReq, JsonNode filter){
        var specs = EmployeeSpecs.instance().fromFilter(filter);
        var page = employeeRepository.findAll(specs, pageReq);
        var count = page.getTotalElements();
        return new CollectionWithCount<>(page.toList(), count);
    }

    public CollectionWithCount<UserLoginAudit> allLogins(PageRequest pageRequest) {
        var page =  auditRepository.findAll(pageRequest);
        return new CollectionWithCount<>(page.toList(), page.getTotalElements());
    }
}
