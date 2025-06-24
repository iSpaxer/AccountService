package com.example.rep;

import com.example.entity.Post;
import com.example.entity.StatusType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends AbstractRepository<Post, Long> {
    List<Post> findByUserIdAndStatus(Long id, StatusType statusType);

    @Query("""
            SELECT post FROM #{#entityName} post
            JOIN post.user user
            WHERE post.id = :id AND post.status <> 'DELETED' AND user.id = :userId
            """)
    Optional<Post> findActiveByIdAndUserId(Long id, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} p SET p.status = 'DELETED', p.deletedDate = CURRENT_TIMESTAMP WHERE p.id = :id AND p.user.id = :userId")
    Integer deleteByIdAndUserId(Long id, Long userId);
}
