package com.example.rep;

import com.example.entity.StatusType;
import com.example.entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends AbstractRepository<User, Long> {

    @Query("""
            SELECT user FROM #{#entityName} user WHERE user.username = :username AND  user.status <> 'DELETED'
            """)
    Optional<User> findActiveByUsername(String username);

    @Query("""
            SELECT user.version FROM #{#entityName} user WHERE user.username = :username AND user.status = :status
            """)
    Optional<Long> existsByUsernameAndStatus(String username, StatusType status);

    Optional<User> findByUsername(String username);
}
