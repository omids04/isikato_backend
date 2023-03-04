package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.IsikatoFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<IsikatoFile, Long> {
}
