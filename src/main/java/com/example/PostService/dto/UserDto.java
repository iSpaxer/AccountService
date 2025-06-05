package com.example.PostService.dto;

import com.example.PostService.entity.Post;
import com.example.PostService.entity.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserDto extends AbstractDto {
        String username;
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password;
        @JsonIgnore
        List<PostDto> posts;

        public UserDto(Long id, StatusType status, LocalDateTime createDate, LocalDateTime lastUpdateDate, String username) {
                this.id = id;
                this.status = status;
                this.createDate = createDate;
                this.lastUpdateDate = lastUpdateDate;
                this.username = username;
        }

}
