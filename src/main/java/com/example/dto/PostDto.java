package com.example.dto;

import com.example.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto extends AbstractDto {

    private String message;
    @JsonIgnore
    private User user;

    public PostDto(Long id, String message, LocalDateTime createdDate, LocalDateTime lastUpdateDate) {
        this.id = id;
        this.message = message;
        this.createDate = createdDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    @NotNull
    @JsonProperty(access = JsonProperty.Access.AUTO)
    public Long getId() {
        return super.getId();
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.AUTO)
    public LocalDateTime getCreateDate() {
        return super.getCreateDate();
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.AUTO)
    public LocalDateTime getLastUpdateDate() {
        return super.getLastUpdateDate();
    }
}
