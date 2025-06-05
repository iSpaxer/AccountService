package com.example.PostService.dto;

import com.example.PostService.entity.StatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AbstractDto {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        protected Long id;
        @JsonIgnore
        protected StatusType status;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        protected LocalDateTime createDate;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        protected LocalDateTime lastUpdateDate;
        @JsonIgnore
        protected LocalDateTime deletedDate;

        public AbstractDto(Long id, StatusType status, LocalDateTime createDate, LocalDateTime lastUpdateDate) {
                this.id = id;
                this.status = status;
                this.createDate = createDate;
                this.lastUpdateDate = lastUpdateDate;
        }

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
