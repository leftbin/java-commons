package com.lbn.commons.fields;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class DnsDomainField implements ResourceField {
    //https://regex101.com/library/SEg6KL
    //validates if the provided dns domain name is valid
    public boolean isValidValue(String dnsDomainName) {
        final String regex = "^(?:[_a-z0-9](?:[_a-z0-9-]{0,61}[a-z0-9])?\\.)+(?:[a-z](?:[a-z0-9-]{0,61}[a-z0-9])?)?$";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(dnsDomainName);
        return matcher.find();
    }
}
