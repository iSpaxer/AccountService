package com.example.PostService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity(name = "posts")
@Table(name = "posts")
@Builder @AllArgsConstructor
@Getter @Setter @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post extends AbstractEntity {

    @Column(nullable = false)
    String message;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    public Post(Long id, String message) {
        this.id = id;
        this.message = message;
    }
}
