package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.Category;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>
        ,JpaSpecificationExecutor<Category> {
    @Transactional
    long removeById(Long id);
}
