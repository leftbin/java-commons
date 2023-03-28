package com.lbn.commons.encoding.base64;

import org.apache.commons.lang3.StringUtils;

//file content encoded into base64 string by the clients might add a prefix that
// needs to be removed before decoding the string.
public class Base64StringFileUploadPrefixRemover {
    public static String remove(String base64String) {
        return StringUtils.removeStartIgnoreCase(base64String, "data:application/json;base64,");
    }
}
