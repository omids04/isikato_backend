package com.isikato.api.model.res;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Permission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record PermissionDetails(long id,
                                JsonNode permissions,
                                List<EmployeeDetails> users,
                                LocalDateTime uDate,
                                LocalDateTime cDate,
                                String description) {


    public static PermissionDetails fromEntity(Permission permission){
        ObjectMapper mapper = new ObjectMapper();
        var users = toEmDetails(permission.getEmployees());
        try {
            return new PermissionDetails(
                    permission.getId(),
                    mapper.readTree(permission.getPermissions()),
                    users,
                    permission.getCreationTime(),
                    permission.getLastModifiedTime(),
                    permission.getDescription());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<EmployeeDetails> toEmDetails(List<Employee> employees) {
        if(employees == null)
            return null;
        return employees
                .stream()
                .map(e -> EmployeeDetails.fromEntity(e, null))
                .collect(Collectors.toList());
    }
}
