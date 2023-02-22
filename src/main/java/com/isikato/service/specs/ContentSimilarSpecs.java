package com.isikato.service.specs;

import com.isikato.infrastructure.entities.Category;
import com.isikato.infrastructure.entities.Content;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ContentSimilarSpecs extends BaseSpecs<Content>{


    private static final ContentSimilarSpecs instance = new ContentSimilarSpecs();

    public static ContentSimilarSpecs instance(){
        return instance;
    }
    public Specification<Content> filter(List<String> tags, List<Long> cats, String page, Long id){
        return Stream
                .of(
                        Stream.of(catsFilter(cats), tagsFilter(tags)).reduce(Specification::or).orElse(null),
                        pageFilter(page),
                        idRemove(id)
                )
                .reduce(Specification::and)
                .orElse(null);

    }

    private Specification<Content> idRemove(Long id) {
        return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
            return cb.not(cb.equal(root.<Long>get("id"), id));
        };
    }

    private Specification<Content> pageFilter(String page) {
        return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
            return cb.equal(root.<String>get("page"), page);
        };
    }

    private Specification<Content> catsFilter(List<Long> cats) {
        return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
            var catRoot = query.from(Category.class);
            var contents = catRoot.<Collection<Content>>get("contents");
            var catIdPre = cb.in(catRoot.get("id"));
            for(var id : cats)
                catIdPre.value(id);
            return cb.and(catIdPre, cb.isMember(root, contents));
        };
    }

    private Specification<Content> tagsFilter(List<String> tags) {
        return (Root<Content> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->{
            var contentTags = root.<Collection<String>>get("tags");
            var ors = new ArrayList<Predicate>();
            for (var tag : tags)
                ors.add(cb.isMember(tag, contentTags));
            return cb.or(ors.toArray(new Predicate[0]));
        };
    }

}
