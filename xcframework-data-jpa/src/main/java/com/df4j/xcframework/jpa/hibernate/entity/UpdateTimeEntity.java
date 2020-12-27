package com.df4j.xcframework.jpa.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public class UpdateTimeEntity extends SuperEntity {
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;
}
