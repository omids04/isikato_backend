package com.isikato.service.specs;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.service.exceptions.EmployeeException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class EmployeeSpecs  extends BaseSpecs<Employee>{


    private static final EmployeeSpecs instance = new EmployeeSpecs();

    public static EmployeeSpecs instance(){
        return instance;
    }
    public Specification<Employee> fromFilter(JsonNode node){
        if(node == null)
            return null;
        return Stream
                .of(
                        nameFilter(node.get("name")),
                        usernameFilter(node.get("username")),
                        emailFilter(node.get("email")),
                        phoneFilter(node.get("phone"))
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);

    }

    private Specification<Employee> usernameFilter(JsonNode node){
        return eqInStringFilter(node, "username");
    }

    private Specification<Employee> phoneFilter(JsonNode node){
        return eqInStringFilter(node, "phone");
    }

    private Specification<Employee> emailFilter(JsonNode node){
        return eqInStringFilter(node, "email");
    }

    private Specification<Employee> nameFilter(JsonNode node) {
        if(node == null)
            return null;
        var operator = getOperatorValue(node);

        var type = operatorType(operator);

        return switch (type){
            case EQ -> (Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var value = node.get("value").asText("");
                var r = root.<String>get("name");
                return cb.equal(r, value);
            };
            case CT -> (Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var value = node.get("value").asText("");
                var r = root.<String>get("name");
                return cb.like(r, "%"+ value +"%");
            };
            case IN -> (Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var r = root.<String>get("name");
                var q = cb.in(r);
                node.get("value").forEach(n -> q.value(n.asText()));
                return q;
            };
            case BT -> throw new EmployeeException("Between Filter Not Possible For Name");
        };
    }


}
