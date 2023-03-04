package com.isikato.service.specs;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Category;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.stream.Stream;

public class CategorySpecs extends BaseSpecs<Category>{


    private static final CategorySpecs instance = new CategorySpecs();

    public static CategorySpecs instance(){
        return instance;
    }
    public Specification<Category> fromFilter(JsonNode node){
        if (node == null)
            return null;
        return Stream
                .of(
                        nameFilter(node.get("name")),
                        typeFilter(node.get("type")),
                        desFilter(node.get("des"))
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);

    }

    private Specification<Category> desFilter(JsonNode node) {
        return ctStringFilter(node, "description");
    }

    private Specification<Category> typeFilter(JsonNode node){
        return eqInStringFilter(node, "type");
    }

    private Specification<Category> nameFilter(JsonNode node){
        return eqInStringFilter(node, "name");
    }


}
