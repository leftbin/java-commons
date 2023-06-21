package com.leftbin.commons.lib.kubernetes.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@Embeddable
public class ContainerResourcesCpuMemoryEmbed {
    private String cpu;
    private String memory;
}
