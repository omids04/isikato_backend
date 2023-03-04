package com.isikato.api.model.req;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Permission;
import io.swagger.v3.core.util.Json;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public record PermissionCreateCommand(long id,
                                      String description,
                                      JsonNode permissions,
                                      List<IdCommand> users) {
    public Permission toEntity(){
        return Permission
                .builder()
                .permissions(permissions.toString())
                .id(id)
                .description(description)
                .employees(users == null? List.of() : users.stream().map(u -> Employee.builder().id(u.id()).build()).collect(Collectors.toList()))
                .build();
    }

}
