package com.leftbin.commons.lib.protobuf;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class ProtobufMessageFieldExtractor {

    /**
     * Extracts the value of the attribute specified in the fieldPath from the message.
     *
     * @param message   The Protobuf message from which to extract data.
     * @param fieldPath The string representing the field path (dot represents sub object of type Message).
     * @return An Optional containing the extracted value if present, or an empty Optional if not found.
     */
    public static String extract(Message message, String fieldPath) {
        String[] parts = fieldPath.split("\\.", 2);

        FieldDescriptor fieldDescriptor = message.getDescriptorForType().findFieldByName(parts[0]);

        if (fieldDescriptor == null) {
            return "";
        }

        Object value = message.getField(fieldDescriptor);

        if (parts.length > 1) {
            if (value instanceof Message) {
                return extract((Message) value, parts[1]);
            } else {
                return "";
            }
        }

        // Check if the value is of type String before returning it
        if (value instanceof String) {
            return value.toString();
        }
        return "";
    }
}
