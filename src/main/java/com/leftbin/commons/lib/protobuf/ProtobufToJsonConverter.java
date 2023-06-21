package com.leftbin.commons.lib.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for converting Protocol Buffer messages to JSON strings.
 */
public class ProtobufToJsonConverter {
    /**
     * Converts a Protocol Buffer message to a JSON string.
     *
     * @param <T> the type of the Protocol Buffer message
     * @param message the Protocol Buffer message to convert
     * @return a JSON string that represents the message
     * @throws InvalidProtocolBufferException if the message cannot be converted to JSON
     */
    public <T extends Message> String convert(T message) throws InvalidProtocolBufferException {
        return JsonFormat.printer().print(message);
    }

    /**
     * Converts a list of Protocol Buffer messages to a single JSON string.
     *
     * @param messages the list of Protocol Buffer messages to convert
     * @return a single JSON string that represents the list of messages
     * @throws InvalidProtocolBufferException if any of the messages cannot be converted to JSON
     */
    public static String convert(List<? extends Message> messages) throws InvalidProtocolBufferException {
        List<String> jsonStrings = messages.stream()
            .map(message -> {
                try {
                    return JsonFormat.printer().print(message);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        return new JSONArray(jsonStrings).toString();
    }

    /**
     * Converts a JSON string to a list of Protocol Buffer messages.
     *
     * @param <T> the type of the Protocol Buffer message
     * @param jsonString the JSON string to convert
     * @param builder an instance of the Builder for the specific type of Protocol Buffer message
     * @return the list of Protocol Buffer messages
     * @throws InvalidProtocolBufferException if the JSON string cannot be parsed to a Message
     * @throws JSONException if the string cannot be converted to a valid JSONArray
     */
    public static <T extends Message> List<T> convert(String jsonString, Message.Builder builder) throws InvalidProtocolBufferException, JSONException {
        JSONArray jsonArray = new JSONArray(jsonString);
        List<T> messages = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JsonFormat.parser().merge(jsonArray.getString(i), builder);
            messages.add((T) builder.build());
            builder.clear();
        }
        return messages;
    }
}
