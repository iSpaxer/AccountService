package com.example.PostService.dto;

import com.example.PostService.entity.StatusType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AbstractDto {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        StatusType status;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime createDate;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime lastUpdateDate;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime deletedDate;

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof AbstractDto that)) return false;
                return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id);
        }
}
