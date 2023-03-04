package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.Permission;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long>
        ,JpaSpecificationExecutor<Permission> {

    @Transactional
    long removeById(Long id);
}
