package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static jakarta.persistence.EnumType.STRING;


@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Version
    protected Long version;

    @Column(nullable = false)
    @Enumerated(STRING)
    protected StatusType status = StatusType.ACTIVE;

    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    protected LocalDateTime createdDate;

    @LastModifiedDate
    protected LocalDateTime lastUpdateDate;

    @Column(name = "deleted_date")
    protected LocalDateTime deletedDate;

    public AbstractEntity(Long id, Long version, StatusType status, LocalDateTime createdDate,
                          LocalDateTime lastUpdateDate, LocalDateTime deletedDate) {
        this.id = id;
        this.version = version;
        this.status = status;
        this.createdDate = createdDate;
        this.lastUpdateDate = lastUpdateDate;
        this.deletedDate = deletedDate;
    }

    public boolean isActive() {
        return status != StatusType.DELETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
