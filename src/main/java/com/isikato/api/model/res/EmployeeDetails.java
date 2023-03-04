package com.isikato.api.model.res;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Permission;

import java.util.List;

public record EmployeeDetails(long id,
                              String username,
                              String name,
                              String email,
                              String phone,
                              List<JsonNode> permissions,
                              boolean enabled,
			      UploadFileResponse image) {

    public static EmployeeDetails fromEntity(Employee em, List<JsonNode> permissions){
        return new EmployeeDetails(
                em.getId(),
                em.getUsername(),
                em.getName(),
                em.getEmail(),
                em.getPhone(),
                permissions,
                em.isEnabled(),
		em.getImage() == null? null: UploadFileResponse.forImage(em.getImage()));
    }
}
