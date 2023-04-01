package com.leftbin.commons.timeline.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "activity")
@Getter
@Setter
@ToString
public class ActivityEntity {
    @Id
    @Column(name = "activity_id")
    private String activityId;
    @Column(name = "activity_type")
    private String activityType;
    @Column(name = "actor")
    private String actor;
    @Column(name = "cre_ts")
    private Timestamp creTs;
    @Column(name = "resource_id")
    private String resourceId;
    @Column(name = "resource_type")
    private String resourceType;
}
