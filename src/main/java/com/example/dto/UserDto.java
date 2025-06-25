package com.example.dto;

import com.example.entity.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends AbstractDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String description;
    @JsonIgnore
    private List<PostDto> posts;

    public UserDto(String username, String password, String description) {
        this.username = username;
        this.password = password;
        this.description = description;
    }

    public UserDto(Long id, StatusType status, LocalDateTime createDate,
                   LocalDateTime lastUpdateDate, String username,
                   String description) {
        this.id = id;
        this.status = status;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.username = username;
        this.description = description;
    }

}
