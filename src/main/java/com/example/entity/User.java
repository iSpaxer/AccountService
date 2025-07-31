package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends AbstractEntity {

    @Column(unique = true, updatable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String description;
    @OneToMany(mappedBy = "user")
    private List<Post> postList;

    public User(Long id, Long version, StatusType status,
                LocalDateTime createdDate,
                LocalDateTime lastUpdateDate, LocalDateTime deletedDate,
                String username,
                String password, String description, List<Post> postList) {
        super(id, version, status, createdDate, lastUpdateDate, deletedDate);
        this.username = username;
        this.password = password;
        this.description = description;
        this.postList = postList;
    }

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(String username, String password, String description) {
        this.username = username;
        this.password = password;
        this.description = description;
    }

}
