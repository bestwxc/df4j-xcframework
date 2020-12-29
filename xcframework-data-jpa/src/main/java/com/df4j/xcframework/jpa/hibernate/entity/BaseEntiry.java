package com.df4j.xcframework.jpa.hibernate.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntiry<T extends Serializable> extends SuperEntity {
    @Id
    private T id;

    @Column(name = "unique_str", nullable = false, length = 80)
    private String uniqueStr;

    @CreatedBy
    @Column(name = "create_by", nullable = false, length = 40)
    private String createBy;

    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @LastModifiedBy
    @Column(name = "update_by", nullable = false, length = 40)
    private String updateBy;

    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getUniqueStr() {
        return uniqueStr;
    }

    public void setUniqueStr(String uniqueStr) {
        this.uniqueStr = uniqueStr;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
