/**
 * Utility class for building SysAudit protobuf messages with updated values.
 *
 * This class is designed to be used with any protobuf message that contains a nested SysAudit message
 * and a protobuf enum indicating the event type. The class provides a method for creating a new version
 * of such a message with updated SysAudit information.
 */
package com.leftbin.commons.lib.audit.library;

import com.leftbin.commons.lib.rpc.request.context.RequestAuthContext;
import com.leftbin.commons.lib.timestamp.CurrentProtobufTimestampGetter;
import com.leftbin.commons.proto.v1.audit.SysAudit;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class CommandSysAuditUpdater {

    /**
     * Create a new version of the provided protobuf message with updated SysAudit information.
     *
     * The provided eventType and existing message are used to create a new message that is identical
     * to the existing message except for the SysAudit information, which is updated.
     *
     * @param eventType a ProtocolMessageEnum indicating the event type
     * @param existing the existing message
     */
    public static <T extends com.google.protobuf.ProtocolMessageEnum, S extends Message.Builder> void update(S existing, T eventType) {
        var currentProtobufTimestamp = CurrentProtobufTimestampGetter.get();
        var by = Optional.ofNullable(RequestAuthContext.getRequestIdentityId()).orElse("");
        var sysAuditBuilder = getSysAudit(existing).toBuilder()
            .setEventType(eventType.getValueDescriptor().getName())
            .setUpdatedAt(currentProtobufTimestamp)
            .setUpdatedBy(by);
        if (StringUtils.isBlank(sysAuditBuilder.getCreatedBy())) {
            sysAuditBuilder.setCreatedAt(currentProtobufTimestamp);
            sysAuditBuilder.setCreatedBy(by);
        }
        setSysAudit(eventType, existing, sysAuditBuilder.build());
    }

    /**
     * Extracts SysAudit from the given protobuf message.
     *
     * The method assumes that the provided message contains a nested field named "status"
     * that itself contains a nested field named "sys_audit" of type SysAudit.
     *
     * @param message the provided protobuf message
     * @return SysAudit object extracted from the message
     */
    private static <S extends Message.Builder> SysAudit getSysAudit(S message) {
        // Accessing the descriptor of the "status" field within the given protobuf message.
        // The descriptor contains metadata about the field, such as its type and whether it's repeated.
        Descriptors.FieldDescriptor statusFieldDescriptor = message.getDescriptorForType().findFieldByName("status");

        // Retrieving the "status" field's value from the message, which is itself another protobuf message.
        Message statusMessage = (Message) message.getField(statusFieldDescriptor);

        // Within the "status" message, accessing the descriptor of the "sys_audit" field.
        Descriptors.FieldDescriptor sysAuditDescriptor = statusMessage.getDescriptorForType().findFieldByName("sys_audit");

        // Retrieving the SysAudit object from the "sys_audit" field within the "status" message.
        return (SysAudit) statusMessage.getField(sysAuditDescriptor);
    }

    /**
     * Updates the SysAudit object and the eventType field in the provided protobuf message.
     *
     * This method assumes that the message has a nested field named "status",
     * and within "status" there is a field named "sys_audit" of type SysAudit.
     * It updates the SysAudit object and the eventType field in the given message.
     *
     * @param eventType The event type value that needs to be set.
     * @param builder The original protobuf message that needs to be updated.
     * @param sysAudit The SysAudit object that needs to be set.
     */
    private static <T extends com.google.protobuf.ProtocolMessageEnum, S extends Message.Builder> void setSysAudit(T eventType, S builder, SysAudit sysAudit) {

        // Getting the field descriptor for the "status" field in the builder.
        Descriptors.FieldDescriptor statusFieldDescriptor = builder.getDescriptorForType().findFieldByName("status");

        // Extracting the "status" field from the builder, which is a nested protobuf message.
        Message statusMessage = (Message) builder.getField(statusFieldDescriptor);

        // Converting the "status" message into a mutable builder to modify its fields.
        Message.Builder statusBuilder = statusMessage.toBuilder();

        // Getting the field descriptor for the "sys_audit" field within the "status" builder.
        Descriptors.FieldDescriptor sysAuditDescriptor = statusBuilder.getDescriptorForType().findFieldByName("sys_audit");

        // Setting the "sys_audit" field in the "status" builder with the provided SysAudit object.
        statusBuilder.setField(sysAuditDescriptor, sysAudit);

        // Setting the "status" field in the main builder with the updated "status" message.
        builder.setField(statusFieldDescriptor, statusBuilder.build());

        // Getting the field descriptor for the "event_type" enum field in the main builder.
        Descriptors.FieldDescriptor eventTypeFieldDescriptor = builder.getDescriptorForType().findFieldByName("event_type");

        // Finding the specific enum value descriptor that matches the provided eventType.
        Descriptors.EnumValueDescriptor enumValueDescriptor = eventTypeFieldDescriptor.getEnumType().findValueByName(eventType.getValueDescriptor().getName());

        // Setting the "event_type" field in the main builder with the selected enum value.
        builder.setField(eventTypeFieldDescriptor, enumValueDescriptor);
    }
}
