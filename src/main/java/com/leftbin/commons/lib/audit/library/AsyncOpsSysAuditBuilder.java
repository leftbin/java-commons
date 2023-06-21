package com.leftbin.commons.lib.audit.library;

import com.leftbin.commons.lib.rpc.security.authentication.microservice.MicroserviceIdentityHolder;
import com.leftbin.commons.lib.timestamp.CurrentProtobufTimestampGetter;
import com.leftbin.commons.proto.v1.audit.SysAudit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsyncOpsSysAuditBuilder {
    private final MicroserviceIdentityHolder microserviceIdentityHolder;
    public SysAudit build(String eventType) {
        var currentProtobufTimestamp = CurrentProtobufTimestampGetter.get();
        var by = microserviceIdentityHolder.getMachineAccountEmail();
        return SysAudit.newBuilder()
            .setEventType(eventType)
            .setCreatedAt(currentProtobufTimestamp)
            .setCreatedBy(by)
            .setUpdatedAt(currentProtobufTimestamp)
            .setUpdatedBy(by)
            .build();
    }
}
