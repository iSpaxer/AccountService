package com.example.rep;

import com.example.entity.AbstractEntity;
import com.example.entity.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface AbstractRepository<T extends AbstractEntity, ID> extends JpaRepository<T, ID> {

    @Query("""
            SELECT abstr FROM #{#entityName} abstr WHERE abstr.id = :id AND  abstr.status <> 'DELETED'
            """)
    Optional<T> findActiveById(Long id);

    @Query("""
            SELECT abstr FROM #{#entityName} abstr WHERE abstr.id = :id AND  abstr.status = 'DELETED'
            """)
    Optional<T> findDeletedById(Long id);

    @Query("""
            SELECT abstr.version FROM #{#entityName} abstr WHERE abstr.id = :id AND abstr.status = :status
            """)
    Optional<Long> existsByIdAndStatus(Long id, StatusType status);

    @Modifying
    @Query("""
            UPDATE #{#entityName} abstr
            SET abstr.status = :status,
            abstr.deletedDate = CASE WHEN :status = 'DELETED' THEN CURRENT_TIMESTAMP ELSE null END
            WHERE abstr.id = :id AND abstr.version = :version
            """)
    int toggleStatus(Long id, Long version, StatusType status);
}
