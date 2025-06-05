package com.example.PostService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity(name = "users")
@Table(name = "users")
@Builder @AllArgsConstructor
@Getter @Setter @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends AbstractEntity {

    @Version
    private Long version;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
