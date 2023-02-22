package com.isikato.api.web;

import com.isikato.api.BaseWebTest;
import com.isikato.api.model.req.EmployeeCreateCommand;
import com.isikato.api.model.req.IdCommand;
import com.isikato.infrastructure.entities.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.StringEndsWith.*;
import static org.hamcrest.core.Is.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeControllerTest extends BaseWebTest {

    @Autowired
    PasswordEncoder passwordEncoder;


    String create_update_url = "/employees/add";
    String remove_url = "/employees/remove";
    String get_all_url = "/employees/getAll";

    String getAllQuery = """
                {
                    "skip": 0,
                    "limit": 10,
                    "filter": %s
                }
                """;

    @Test
    void callingCreateUpdateApiWithoutAuthentication() throws Exception {
        //given
        var creationModel = new EmployeeCreateCommand("username", "mypassword", "name", "dasfa", "092155555555", true);
        var body = toJsonString(creationModel);

        //when
        var res = sendRq(body, create_update_url);

        //then
        res.expectStatus().isForbidden();
    }

    @Test
    void callingCreateUpdateApiWithBadFormattedEmail() throws Exception {
        //given
        var creationModel = new EmployeeCreateCommand("username", "mypassword", "name", "dasfa", "092155555555", true);
        var body = toJsonString(creationModel);
        var token = createTokenForTest();
        var countBefore = employeeRepository.count();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.path").value(endsWith(this.create_update_url))
                .jsonPath("$.timestamp").exists();
        assertEquals(countBefore, employeeRepository.count());

    }

    @Test
    void tryingToCreateAnEmployeeWithShortUsername() throws Exception {
        //given
        var creationModel = new EmployeeCreateCommand("o   ", "mypassword", "name", "email@gmail.com", "092155555555", true);
        var body = toJsonString(creationModel);
        var token = createTokenForTest();
        var countBefore = employeeRepository.count();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.path").value(endsWith(this.create_update_url))
                .jsonPath("$.timestamp").exists();
        assertEquals(countBefore, employeeRepository.count());

    }

    @Test
    void tryingToCreateAnEmployeeWithShortPassword() throws Exception {
        //given
        var creationModel = new EmployeeCreateCommand("omidsss", "myrd", "name", "email@gmail.com", "092155555555", true);
        var body = toJsonString(creationModel);
        var token = createTokenForTest();
        var countBefore = employeeRepository.count();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.path").value(endsWith(this.create_update_url))
                .jsonPath("$.timestamp").exists();
        assertEquals(countBefore, employeeRepository.count());

    }

    @Test
    void tryingToCreateAnEmployeeWithDuplicateUsername() throws Exception {
        //given
        var username = UUID.randomUUID().toString();
        var creationModel = new EmployeeCreateCommand(username, "myPassword", "name", "email@gmail.com", "092155555555", true);
        var em = Employee.builder().username(username).password("somePass").build();
        employeeRepository.save(em);

        var body = toJsonString(creationModel);
        var token = createTokenForTest();
        var countBefore = employeeRepository.count();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.path").value(endsWith(this.create_update_url))
                .jsonPath("$.timestamp").exists();
        assertEquals(countBefore, employeeRepository.count());

    }

    @Test
    void creatingAnEmployee() throws Exception {
        //given
        var username = UUID.randomUUID().toString();
        var creationModel = new EmployeeCreateCommand(username, "myrjskljdd", "name", "emaomiil@gmail.com", "0921554455555", true);
        var body = toJsonString(creationModel);
        var token = createTokenForTest();
        var countBefore = employeeRepository.count();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists();
        assertEquals(countBefore + 1, employeeRepository.count());
    }

    @Test
    void editingAnEmployeeWithoutChangingPassword() throws Exception {
        //given
        var oldUsername = UUID.randomUUID().toString();
        var oldPassword = UUID.randomUUID().toString();
        var newUsername = UUID.randomUUID().toString();
        var em = Employee.builder().username(oldUsername).password(oldPassword).build();
        em = employeeRepository.save(em);
        var command = new EmployeeCreateCommand(em.getId(), newUsername,null, "name", "email@gmail.com", "0921554555555", true);

        var body = toJsonString(command);
        var token = createTokenForTest();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists();

        var updated = employeeRepository.findById(em.getId());
        assertEquals(command.username(), updated.get().getUsername());
        assertEquals(em.getPassword(), updated.get().getPassword());
    }

    @Test
    void editingAnEmployeeWithChangingPassword() throws Exception {

        //given
        var oldUsername = UUID.randomUUID().toString();
        var oldPassword = UUID.randomUUID().toString();
        var newUsername = UUID.randomUUID().toString();
        var newPassword = UUID.randomUUID().toString();
        var em = Employee.builder().username(oldUsername).password(oldPassword).build();
        em = employeeRepository.save(em);
        var command = new EmployeeCreateCommand(em.getId(), newUsername, newPassword, "name", "emaoooil@gmail.com", "09215555555", true);

        var body = toJsonString(command);
        var token = createTokenForTest();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists();

        var updated = employeeRepository.findById(em.getId());
        assertEquals(command.username(), updated.get().getUsername());
        assertTrue(passwordEncoder.matches(command.password(), updated.get().getPassword()));
    }

    @Test
    void editingAnEmployeeThatDoesNotExists() throws Exception {

        //given
        var command = new EmployeeCreateCommand(100005L, "newUsername","newPassword", "name", "emffail@gmail.com", "09215355555", true);

        var body = toJsonString(command);
        var token = createTokenForTest();

        //when
        var res = sendRq(body, token, create_update_url);

        //then
        res
                .expectStatus().isBadRequest();
    }

    @Test
    void removingAnEmployee() throws Exception {
        //given
        var em = Employee.builder().username("myUsername").password("somePass").build();
        em = employeeRepository.save(em);
        var command  = new IdCommand(em.getId());
        var body = toJsonString(command);
        var token = createTokenForTest();
        var countBefore = employeeRepository.count();

        //when
        var res = sendRq(body, token, remove_url);

        //then
        res.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success", true);
        assertEquals(countBefore - 1, employeeRepository.count());

    }

    @Test
    void tryingToRemoveAnEmployeeThatDoesNotExists() throws Exception {
        //given

        var command  = new IdCommand(10000L);
        var body = toJsonString(command);
        var token = createTokenForTest();
        var countBefore = employeeRepository.count();

        //when
        var res = sendRq(body, token, remove_url);

        //then
        res.expectStatus().isOk()
                        .expectBody()
                .jsonPath("$.success").value(is(false));
        assertEquals(countBefore , employeeRepository.count());

    }

    String createTestDataForNameFilter(){
        var name1 = UUID.randomUUID().toString();
        var name2 = UUID.randomUUID().toString();
        var em1 = Employee
                .builder()
                .username(UUID.randomUUID().toString())
                .password("pass")
                .name(name1)
                .build();
        var em2 = Employee
                .builder()
                .username(UUID.randomUUID().toString())
                .password("pass")
                .name(name2)
                .build();
        employeeRepository.saveAll(List.of(em1, em2));
        return name1;
    }

    @Test
    void nameEqFilterTest(){

        //given
        var name = createTestDataForNameFilter();
        var filter = """
                {
                    "name":{
                        "operator": "=",
                        "value": "%s"
                    }
                }
                """;
        filter = filter.formatted(name);

        var query = getAllQuery.formatted(filter);
        var token = createTokenForTest();

        //when
        var res = sendRq(query, token, get_all_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").value(is(1))
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].name").value(is(name))
                .jsonPath("$.data[1]").doesNotExist();
    }

    @Test
    void nameCTFilterTest(){
        var name = createTestDataForNameFilter();

        //given
           var filter = """
                {
                    "name":{
                        "operator": "contains",
                        "value": "%s"
                    }
                }
                """;
        filter = filter.formatted(name.substring(1, 10));
        var query = getAllQuery.formatted(filter);
        var token = createTokenForTest();

        //when
        var res = sendRq(query, token, get_all_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").value(is(1))
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].name").value(is(name))
                .jsonPath("$.data[1]").doesNotExist();
    }

    @Test
    void nameInFilterTest(){
        var name = createTestDataForNameFilter();

        //given
        var filter = """
                {
                    "name":{
                        "operator": "in",
                        "value": ["%s", "some Other name"]
                    }
                }
                """;
        filter = filter.formatted(name);
        var query = getAllQuery.formatted(filter);
        var token = createTokenForTest();

        //when
        var res = sendRq(query, token, get_all_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").value(is(1))
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].name").value(is(name))
                .jsonPath("$.data[1]").doesNotExist();
    }

    String createTestDataForUsernameFilter(){
        var u1 = UUID.randomUUID().toString();
        var u2 = UUID.randomUUID().toString();
        var em1 = Employee.builder().username(u1).password("pass").build();
        var em2 = Employee.builder().username(u2).password("pass").build();
        employeeRepository.saveAll(List.of(em1, em2));
        return u1;
    }


    @Test
    void usernameEqFilterTest(){

        //given
        var username = createTestDataForUsernameFilter();
        var filter = """
                {
                    "username":{
                        "value": "%s"
                    }
                }
                """;
        filter = filter.formatted(username);

        var query = getAllQuery.formatted(filter);
        var token = createTokenForTest();

        //when
        var res = sendRq(query, token, get_all_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").value(is(1))
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].username").value(is(username))
                .jsonPath("$.data[1]").doesNotExist();
    }


    @Test
    void usernameInFilterTest(){
        var username = createTestDataForUsernameFilter();

        //given
        var filter = """
                {
                    "username":{
                        "operator": "in",
                        "value": ["%s", "some_Other_username"]
                    }
                }
                """;
        filter = filter.formatted(username);
        var query = getAllQuery.formatted(filter);
        var token = createTokenForTest();

        //when
        var res = sendRq(query, token, get_all_url);

        //then
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.total").value(is(1))
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].username").value(is(username))
                .jsonPath("$.data[1]").doesNotExist();
    }


}