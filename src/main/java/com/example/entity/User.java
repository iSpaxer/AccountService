package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Entity(name = "users")
@Table(name = "users")
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends AbstractEntity {

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Post> postList;

    public void createPost(Post post) {
        if (!Objects.equals(post.getId(), id)) {
            post.setId(id);
        }
        postList.add(post);
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(Long id) {
        super();
    }

    public URI getURI() {
        return URI.create("/user/" + id);
    }
}
