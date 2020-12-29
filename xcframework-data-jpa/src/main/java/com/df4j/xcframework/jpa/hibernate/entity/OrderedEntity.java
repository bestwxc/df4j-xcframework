package com.df4j.xcframework.jpa.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class OrderedEntity<T extends Serializable> extends AuditableEntity<T>{

    @Column(name = "order_num", nullable = false)
    private Integer orderNum;

    @Column(name = "state", nullable = false)
    private Integer state;

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
