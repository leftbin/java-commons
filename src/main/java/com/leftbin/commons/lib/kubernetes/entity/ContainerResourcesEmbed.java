package com.leftbin.commons.lib.kubernetes.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Embeddable
public class ContainerResourcesEmbed {
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cpu", column = @Column(name = "container_cpu_limit")),
        @AttributeOverride(name = "memory", column = @Column(name = "container_memory_limit"))
    })
    ContainerResourcesCpuMemoryEmbed limits;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cpu", column = @Column(name = "container_cpu_request")),
        @AttributeOverride(name = "memory", column = @Column(name = "container_memory_request"))
    })
    ContainerResourcesCpuMemoryEmbed requests;
}
