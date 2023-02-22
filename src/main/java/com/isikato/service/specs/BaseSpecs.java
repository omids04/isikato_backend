package com.isikato.service.specs;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Permission;
import com.isikato.service.exceptions.EmployeeException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class BaseSpecs<T> {

    protected OperatorType operatorType(String operator){
        if(operator == null)
            return OperatorType.EQ;
        return switch (operator) {
            case "exact", "=", "" -> OperatorType.EQ;
            case "contains" -> OperatorType.CT;
            case "in" -> OperatorType.IN;
            default -> OperatorType.BT;
        };
    }

    protected String getOperatorValue(JsonNode node) {
        return node.get("operator") == null? "=" : node.get("operator").asText().toLowerCase();
    }

    protected enum OperatorType{
        EQ, IN, BT, CT
    }

    protected Specification<T> idFilter(JsonNode node){
        if(node == null)
            return null;
        var operator = getOperatorValue(node);

        var type = operatorType(operator);

        return switch (type){
            case IN -> (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var r = root.<T>get("employees").<Long>get("id");
                var q = cb.in(r);
                node.get("value").forEach(n -> q.value(n.get("id").asLong()));
                return q;
            };
            case BT -> throw new EmployeeException("Between Filter Not Possible For users");
            case CT -> throw new EmployeeException("contains Filter Not Possible For users");
            case EQ -> throw new EmployeeException("equal Filter Not Possible For users");
        };
    }


    protected Specification<T> eqInStringFilter(JsonNode node, String attribute){
        if(node == null)
            return null;
        var operator = getOperatorValue(node);

        var type = operatorType(operator);

        return switch (type){
            case EQ -> (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var value = node.get("value").asText("");
                var r = root.<String>get(attribute);
                return cb.equal(r, value);
            };
            case IN -> (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var r = root.<String>get(attribute);
                var q = cb.in(r);
                node.get("value").forEach(n -> q.value(n.asText()));
                return q;
            };
            case BT -> throw new EmployeeException("Between Filter Not Possible For %s".formatted(attribute));
            case CT -> throw new EmployeeException("contains Filter Not Possible For %s".formatted(attribute));
        };
    }

    protected Specification<T> ctStringFilter(JsonNode node, String attribute){
        if(node == null)
            return null;
        var operator = getOperatorValue(node);
        var type = operatorType(operator);

        return switch (type){
            case CT -> (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var value = node.get("value").asText("");
                var r = root.<String>get(attribute);
                return cb.like(r, "%"+ value +"%");
            };
            case BT -> throw new EmployeeException("Between Filter Not Possible For %s".formatted(attribute));
            case IN -> throw new EmployeeException("In Filter Not Possible For %s".formatted(attribute));
            case EQ -> throw new EmployeeException("Equal Filter Not Possible For %s".formatted(attribute));
        };
    }

}
