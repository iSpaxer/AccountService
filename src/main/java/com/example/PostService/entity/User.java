package com.example.PostService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity(name = "users")
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
@SQLRestriction("status <> 'DELETED'")
@SQLDelete(sql = "UPDATE users SET status = 'DELETED' WHERE id = ?")
public class User extends AbstractEntity {

    @Column(unique = true)
    private String username;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

}
