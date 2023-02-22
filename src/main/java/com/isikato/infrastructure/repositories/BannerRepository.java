package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.Banner;
import com.isikato.infrastructure.entities.Category;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BannerRepository extends PagingAndSortingRepository<Banner, Long>
        ,JpaSpecificationExecutor<Banner> {
    @Transactional
    long removeById(Long id);
}
