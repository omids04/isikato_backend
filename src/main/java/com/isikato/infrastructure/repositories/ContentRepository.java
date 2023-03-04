package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContentRepository extends PagingAndSortingRepository<Content, Long>,
        JpaSpecificationExecutor<Content> {
    @Query(nativeQuery = true, value = "UPDATE isikato_content SET download_counter = download_counter + 1 WHERE id = :id")
    @Modifying
    void incDownloadById(@Param("id") long id);

}
