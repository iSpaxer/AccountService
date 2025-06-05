package com.example.PostService.rep;

import com.example.PostService.entity.AbstractEntity;
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

}
