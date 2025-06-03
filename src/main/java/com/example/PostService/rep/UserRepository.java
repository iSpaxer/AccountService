package com.example.PostService.rep;

import com.example.PostService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT user FROM #{#entityName} user WHERE user.status = 'DELETED'
            """)
    List<User> findAllDeleted();

    @Modifying
    @Query("""
            UPDATE #{#entityName} user SET user.status = 'ACTIVE' WHERE user.id = :id
            """)
    void restoreById(Long id);

}
