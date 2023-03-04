package com.isikato.service.specs;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Category;
import com.isikato.infrastructure.entities.Content;
import com.isikato.infrastructure.entities.Employee;
import com.isikato.infrastructure.entities.Permission;
import com.isikato.service.exceptions.EmployeeException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ContentSpecs extends BaseSpecs<Content>{


    private static final ContentSpecs instance = new ContentSpecs();

    public static ContentSpecs instance(){
        return instance;
    }
    public Specification<Content> fromFilter(JsonNode node){
        if (node == null)
            return null;
        return Stream
                .of(
                        titleFilter(node.get("title")),
//                        bodyFilter(node.get("body")),
                        descriptionFilter(node.get("description")),
                        pageFilter(node.get("page")),
                        catsFilter(node.get("categories")),
                        tagsFilter(node.get("tags")),
                        featuredFilter(node.get("featured"))
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);

    }

    private Specification<Content> tagsFilter(JsonNode node) {
        return eqInStringFilter(node, "tags");
    }

    private Specification<Content> catsFilter(JsonNode node) {

        if(node == null)
            return null;
        var operator = getOperatorValue(node);

        var type = operatorType(operator);
        if(type != OperatorType.IN)
            return null;
        return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
            query.distinct(true);

            var catRoot = query.from(Category.class);
            var contents = catRoot.<Collection<Content>>get("contents");
            var q = cb.<Long>in(catRoot.get("id"));
            node.get("value").forEach(n -> q.value(n.get("id").asLong()));
            return cb.and(q, cb.isMember(root, contents));
        };
    }
    private Specification<Content> titleFilter(JsonNode node) {
        return ctStringFilter(node, "title");
    }

//    private Specification<Content> bodyFilter(JsonNode node){
//        return ctStringFilter(node, "body");
//    }

    private Specification<Content> descriptionFilter(JsonNode node){
        return ctStringFilter(node, "description");

    }

    private Specification<Content> pageFilter(JsonNode node){
        return ctStringFilter(node, "page");
    }

    private Specification<Content> featuredFilter(JsonNode node) {
        if(node == null)
            return null;
        var operator = getOperatorValue(node);

        var type = operatorType(operator);

        return switch (type){
            case EQ -> (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
                var value = node.get("value").asBoolean();
                var r = root.<Boolean>get("featured");
                return cb.equal(r, value);
            };
            case BT -> throw new EmployeeException("Between Filter Not Possible For Name");
            case CT -> throw new EmployeeException("contains Filter Not Possible For featured");
            case IN -> throw new EmployeeException("in Filter Not Possible For featured");
        };
    }

}
