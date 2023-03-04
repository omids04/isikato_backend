package com.isikato.service.specs;

import com.isikato.infrastructure.entities.Category;
import com.isikato.infrastructure.entities.Content;
import com.isikato.infrastructure.entities.ContentVisit;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class ContentVisitSpecs{


    private static final ContentVisitSpecs instance = new ContentVisitSpecs();

    public static ContentVisitSpecs instance(){
        return instance;
    }

    public Specification<Content> from(String page, LocalDateTime start, LocalDateTime end){
        return Stream
                .of(
                        pageFilter(page),
                        dateFilter(start, end)
                )
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);

    }

    private Specification<Content> dateFilter(LocalDateTime start, LocalDateTime end) {
        return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
            query.distinct(true);
            var visit = query.from(ContentVisit.class);
            var content = visit.<Content>get("content");
            return cb.and(cb.between(visit.get("timestamp"), start, end), cb.equal(root, content));
        };
    }

    protected Specification<Content> pageFilter(String page){
        if(page == null)
            return null;
        return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
            var r = root.<String>get("page");
            return cb.equal(r, page);
        };
    }



}
