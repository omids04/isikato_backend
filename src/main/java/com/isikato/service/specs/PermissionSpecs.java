package com.isikato.service.specs;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Permission;
import com.isikato.service.exceptions.EmployeeException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public class PermissionSpecs extends BaseSpecs<Permission>{

    private static final PermissionSpecs instance = new PermissionSpecs();

    public static PermissionSpecs instance(){
        return instance;
    }

    public Specification<Permission> fromFilter(JsonNode node){
        if (node == null)
            return null;

        return Stream
                .of(
                        desFilter(node.get("description")),
//                        permissionsFilter(node.get("permissions")),
                        employeesFilter(node.get("users")),
                        createDateFilter(node.get("cDate")),
                        updateDateFilter(node.get("uDate"))
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);
    }

    private Specification<Permission> updateDateFilter(JsonNode node) {
        return btDateFilter(node, "lastModifiedTime");
    }

    private Specification<Permission> createDateFilter(JsonNode node) {
        return btDateFilter(node, "creationTime");
    }

    private Specification<Permission> employeesFilter(JsonNode node){
        return idFilter(node);
    }

    private Specification<Permission> btDateFilter(JsonNode node, String attribute){
        if(node == null)
            return null;
        var operator = getOperatorValue(node);

        var type = operatorType(operator);

        return switch (type){
            case CT -> (Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var value1 = LocalDateTime.parse(node.get("value").get(0).asText());
                var value2 = LocalDateTime.parse(node.get("value").get(1).asText());
                var r = root.<LocalDateTime>get(attribute);
                return cb.between(r, value1, value2);
            };
            case BT -> throw new EmployeeException("Between Filter Not Possible For %s".formatted(attribute));
            case IN -> throw new EmployeeException("In Filter Not Possible For %s".formatted(attribute));
            case EQ -> throw new EmployeeException("Equal Filter Not Possible For %s".formatted(attribute));
        };
    }

    private Specification<Permission> desFilter(JsonNode node){
        return ctStringFilter(node, "description");
    }

//    private Specification<Permission> permissionsFilter(JsonNode node){
//        return ctStringFilter(node, "permissions");
//    }


}
