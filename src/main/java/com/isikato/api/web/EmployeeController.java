package com.isikato.api.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.api.model.req.IdCommand;
import com.isikato.api.model.req.EmployeeCreateCommand;
import com.isikato.api.model.req.QueryModel;
import com.isikato.api.model.res.CollectionWithTotalModel;
import com.isikato.api.model.res.LoginAuditModel;
import com.isikato.api.model.res.RemovedModel;
import com.isikato.api.model.res.EmployeeDetails;
import com.isikato.infrastructure.entities.Permission;
import com.isikato.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ObjectMapper mapper;

    @PostMapping(value = "add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDetails createOrUpdate(@RequestBody @Valid EmployeeCreateCommand model){
        return EmployeeDetails
                .fromEntity(employeeService.createOrUpdate(model.toEntity()), null);
    }

    @PostMapping(value = "remove",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RemovedModel remove(@RequestBody IdCommand model){
        var result = employeeService.remove(model.id());
        return new RemovedModel(result);
    }

    @PostMapping(value = "getInfo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDetails getOne(Authentication authentication){
        var result = employeeService.get((String) authentication.getPrincipal());
        var permissions = result
                .getPermissions()
                .stream()
                .map(Permission::getPermissions)
                .map(p -> {
                    try {
                        return mapper.readTree(p);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return EmployeeDetails.fromEntity(result, permissions);
    }

    @PostMapping(value = "getAll",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionWithTotalModel<EmployeeDetails> getList(@RequestBody(required = false) Optional<QueryModel> model){
        int limit = 10, skip = 0;
        JsonNode node = null;
        if(model.isPresent()){
            limit = model.get().limit() == 0 ? 10 : model.get().limit();
            skip = model.get().skip();
            node = model.get().filter();
        }
        var results = employeeService.getAll(PageRequest.of(skip, limit), node);
        var data = results
                .getData()
                .stream()
                .map(e -> EmployeeDetails.fromEntity(e, null))
                .collect(Collectors.toList());
        return new CollectionWithTotalModel<>(data, results.getTotal());
    }


    @PostMapping(value = "logins",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionWithTotalModel<LoginAuditModel> logins(@RequestBody(required = false) Optional<QueryModel> model){
        int limit = 10, skip = 0;
        if(model.isPresent()){
            limit = model.get().limit() == 0 ? 10 : model.get().limit();
            skip = model.get().skip();
        }
        var results = employeeService.allLogins(PageRequest.of(skip, limit, Sort.by("id").descending()));
        var data = results
                .getData()
                .stream()
                .map(e -> new LoginAuditModel(e.getUser().getId(), e.getLoginDate()))
                .collect(Collectors.toList());
        return new CollectionWithTotalModel<>(data, results.getTotal());
    }
}
