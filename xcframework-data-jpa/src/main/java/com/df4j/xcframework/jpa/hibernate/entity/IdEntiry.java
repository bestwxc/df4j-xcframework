package com.df4j.xcframework.jpa.hibernate.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class IdEntiry<T extends Serializable> extends SuperEntity {

    @Id
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
