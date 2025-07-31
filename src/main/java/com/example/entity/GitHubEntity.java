package com.example.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "github_entity")
@Table(name = "github_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitHubEntity {

    @Id
    @Column(name = "github_id")
    private Long gitHubId;
    private String login;
    private String avatarUrl;
    private String name;
    private String location;
    private Instant createdAt;

    @Override
    public String toString() {
        return "GitHubEntity{" +
                "login='" + login + '\'' +
                ", githubId=" + gitHubId +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
