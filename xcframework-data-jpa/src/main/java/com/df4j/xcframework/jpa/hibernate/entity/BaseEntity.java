package com.df4j.xcframework.jpa.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class BaseEntity<T extends Serializable> implements Serializable {

    @Id
    private T id;

    @Column(name = "unique_str", nullable = false, length = 80, unique = true)
    private String uniqueStr;

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
}
