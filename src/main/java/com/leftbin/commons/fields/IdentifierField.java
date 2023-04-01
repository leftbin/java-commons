package com.leftbin.commons.fields;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class IdentifierField implements ResourceField {
    //validates if the provided identifier string only
    // contains
    //1. lowercase letters
    //2. numbers
    //3. hyphens
    //and do not contain
    // 1. hyphen at the end
    public boolean isValidValue(String identifierValue) {
        final String HYPHEN_CHARACTER = "-";
        if (StringUtils.endsWith(identifierValue, HYPHEN_CHARACTER)) {
            //identifier can not end with a hyphen
            return false;
        }
        final String regex = "[^a-z0-9-]";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(identifierValue);
        //the regex is a negation. valid ids will not have any matches in the input.
        //if matcher finds any matches then it means that the input has characters other
        // than alphanumeric(lowercase) and hyphen.
        return !matcher.find();
    }
}
