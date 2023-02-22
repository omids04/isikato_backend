package com.isikato.service;

import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.repositories.EmployeeRepository;
import com.isikato.service.exceptions.EmployeeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {


    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    PasswordEncoder passwordEncoder;


    @InjectMocks
    EmployeeService service;

    String username = "username";
    String password = "password";
    String name = "name";

    @Test
    void whenEmployeeHasNoIdITMeansANewOneShouldGetsCreated() {
        //given
        var user = Employee.builder().username(username).password(password).name(name).build();
        var answer = Employee.builder().username(username).password(passwordEncoder.encode(password)).name(name).id(5L).build();
        when(employeeRepository.save(any())).thenReturn(answer);

        //when
        var created = service.createOrUpdate(user);

        //then
        assertEquals(answer.getId(), created.getId());
        assertEquals(passwordEncoder.encode(password), created.getPassword());
        assertEquals(username, created.getUsername());
        assertEquals(name, created.getName());
        verify(employeeRepository, times(1)).save(any());
    }

    @Test
    void whenEmployeeWithGivenIdDoesNotExists() {
        //given
        var em = Employee.builder().id(1L).username(username).password(password).name(name).build();
        when(employeeRepository.updateWithPassword(any())).thenReturn(0);

        //when
        //then
        assertThrows(EmployeeException.class, () -> service.createOrUpdate(em));
    }

    @Test
    void whenCreatingProvidedUsernameShouldNotAlreadyExist() {
        //given
        var user = Employee.builder().username(username).password(password).name(name).build();
        when(employeeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        //when
        //then
        assertThrows(DataIntegrityViolationException.class, () -> service.createOrUpdate(user));

    }

    @Test
    void passwordIsMandatoryForEmployeeCreation() {
        //given
        var dto = Employee.builder().username(username).name(name).build();

        //when
        //then
        assertThrows(EmployeeException.class, () -> service.createOrUpdate(dto));
    }

    @Test
    void usernameIsMandatoryForEmployeeCreation() {
        //given
        var dto = Employee.builder().password(password).name(name).build();

        //when
        //then
        assertThrows(EmployeeException.class, () -> service.createOrUpdate(dto));
    }

    @Test
    void whenEmployeeWithGivenIdExistsItShouldGetsUpdated() {
        //given
        var newEm = Employee.builder().id(1L).username(username).password(password).name(name).build();
        when(employeeRepository.updateWithPassword(any())).thenReturn(1);

        //when
        service.createOrUpdate(newEm);

        //then
        verify(employeeRepository, times(1)).updateWithPassword(eq(newEm));
    }

    @Test
    void whenUpdatingIfPasswordIsNotProvidedMeansPasswordShouldNotChange(){
        //given
        var newEm = Employee.builder().id(1L).username(username).name(name).build();
        when(employeeRepository.update(any())).thenReturn(1);

        //when
        service.createOrUpdate(newEm);

        //then
        verify(employeeRepository, times(1)).update(any());
        verify(employeeRepository, never()).updateWithPassword(any());
    }

    @Test
    void passwordShouldGetsEncryptedBeforePersistingToDb(){
        //given
        var em = Employee.builder().username(username).password(password).id(1L).build();
        when(passwordEncoder.encode(any())).thenReturn("this is encoded password");
        when(employeeRepository.updateWithPassword(any())).thenReturn(1);


        //when
        service.createOrUpdate(em);

        //then
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    void whenUserWithGivenIdExistItShouldGetsRemovedFromDb(){
        //given
        when(employeeRepository.removeById(1L)).thenReturn(1L);

        //when
        boolean deleted = service.remove(1L);

        //then
        verify(employeeRepository, times(1)).removeById(eq(1L));
        assertTrue(deleted);
    }

    @Test
    void whenUserWithGivenUsernameDoesNotExistedForRemoval(){
        //given
        when(employeeRepository.removeById(1L)).thenReturn(0L);

        //when
        boolean deleted = service.remove(1L);

        //then
        verify(employeeRepository, times(1)).removeById(eq(1L));
        assertFalse(deleted);
    }


}