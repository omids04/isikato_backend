package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FileDataRepository extends JpaRepository<FileData, Long> {

    Optional<FileData> findByTypeAndFileId(FileData.Type type, long fileId);
}
