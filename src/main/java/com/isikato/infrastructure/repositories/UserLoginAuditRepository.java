package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.UserLoginAudit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLoginAuditRepository extends JpaRepository<UserLoginAudit, Long> {

    List<UserLoginAudit> findAllByUser_Id(long id, Pageable pageable);
}