package com.leftbin.commons.lib.kubernetes.converter;

import com.leftbin.commons.lib.protobuf.ProtobufToJsonConverter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.leftbin.commons.proto.v1.kubernetes.Container;
import org.json.JSONException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class ContainerListToJsonStringEntityAttributeConverter implements AttributeConverter<List<Container>, String> {
    @Override
    public String convertToDatabaseColumn(List<Container> attribute) {
        try {
            return ProtobufToJsonConverter.convert(attribute);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new RuntimeException("failed converting sidecars list to json", e);
        }
    }

    @Override
    public List<Container> convertToEntityAttribute(String dbData) {
        try {
            return ProtobufToJsonConverter.convert(dbData, Container.newBuilder());
        } catch (InvalidProtocolBufferException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("failed converting json to sidecars list", e);
        }
    }
}
