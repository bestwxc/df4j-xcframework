package com.df4j.xcframework.jpa.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class UniqueStrEntity extends SuperEntity {
    @Column(name = "unique_str", nullable = false, length = 80)
    private String uniqueStr;
}
