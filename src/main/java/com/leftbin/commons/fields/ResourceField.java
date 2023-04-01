package com.leftbin.commons.fields;

public interface ResourceField {
    // confirms if the provided input value passes all the validations.
    boolean isValidValue(String fieldValue);
}
