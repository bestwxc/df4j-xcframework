package com.df4j.xcframework.jpa.hibernate.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public class AuditableEntity<T extends Serializable> extends BaseEntity<T> {

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 40)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = false, length = 40)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_time", nullable = false)
    private LocalDateTime lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
