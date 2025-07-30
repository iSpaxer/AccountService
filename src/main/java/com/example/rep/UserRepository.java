package com.example.rep;

import com.example.entity.StatusType;
import com.example.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends AbstractRepository<User, Long>, UserRepositoryAdvanced {

    Optional<User> findByIdAndStatus(Long id, StatusType status);

    Optional<User> findByUsernameAndStatus(String username, StatusType status);

    Optional<Long> findVersionByUsernameAndStatus(String username, StatusType status);


    Optional<User> findByGitHub_GitHubId(Long gitHubId);

    @Modifying
    @Query("""
            UPDATE #{#entityName} user
            SET user.status = :status,
            user.deletedDate = CASE WHEN :status = 'DELETED' THEN CURRENT_TIMESTAMP ELSE null END
            WHERE user.username = :username AND user.version = :version
            """)
    int toggleStatus(String username, Long version, StatusType status);


}
