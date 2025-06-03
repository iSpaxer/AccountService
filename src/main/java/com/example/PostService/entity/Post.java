package com.example.PostService.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@NoArgsConstructor
@Entity(name = "posts")
@Table(name = "posts")
@SQLRestriction("StatusType <> 'DELETED'")
@SQLDelete(sql = "UPDATE users SET status = 'DELETED' WHERE id = ?")
public class Post extends AbstractEntity {

    @Column(nullable = false)
    String message;
    @ManyToOne()
    User user;

}
