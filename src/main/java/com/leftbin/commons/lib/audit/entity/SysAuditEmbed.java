package com.leftbin.commons.lib.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
public class SysAuditEmbed {
    @Column(name = "created_by")
    protected String createdBy;
    @Column(name = "created_at")
    protected Timestamp createdAt;
    @Column(name = "updated_by")
    protected String updatedBy;
    @Column(name = "updated_at")
    protected Timestamp updatedAt;
    @Column(name = "event_type")
    protected String eventType;
}
