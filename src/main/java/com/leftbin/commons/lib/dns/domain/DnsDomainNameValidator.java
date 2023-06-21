package com.leftbin.commons.lib.dns.domain;

import com.leftbin.commons.lib.step.StepResult;

import java.util.Objects;
import java.util.regex.Pattern;

public class DnsDomainNameValidator {

    public static final String NULL_DOMAIN_NAME_ERROR = "domain-name is null";
    public static final String INVALID_DNS_DOMAIN_NAME_ERROR = "Invalid DNS Domain Name";

    //https://regex101.com/library/SEg6KL
    /**
     * Regular expression to validate a string as a valid DNS name.
     *
     * ^                                     - Start of the string
     * (?:                                   - Start of non-capturing group
     *    [_a-z0-9]                          - Allow lowercase letters, digits, and underscore as the first character
     *    (?:[_a-z0-9-]{0,61}[a-z0-9])?      - Allow lowercase letters, digits, underscore, and hyphen, with a length of 1 to 63 characters
     *    \.                                 - Match a dot (for subdomains)
     * )+                                    - End of non-capturing group, allowing one or more occurrences
     * (?:                                   - Start of non-capturing group (for top-level domain)
     *    [a-z]                             - Allow lowercase letters as the first character
     *    (?:[a-z0-9-]{0,61}[a-z0-9])?       - Allow lowercase letters, digits, and hyphen, with a length of 1 to 63 characters
     * )?                                    - End of non-capturing group, making it optional
     * $                                     - End of the string
     */
    private final static String REGEX = "^(?:[_a-z0-9](?:[_a-z0-9-]{0,61}[a-z0-9])?\\.)+(?:[a-z](?:[a-z0-9-]{0,61}[a-z0-9])?)?$";

    //validates if the provided dns domain name is valid
    public static StepResult validate(String dnsDomainName) {
        var builder = StepResult.newBuilder();
        if(Objects.isNull(dnsDomainName)) {
            return builder.error(NULL_DOMAIN_NAME_ERROR).build();
        }

        var pattern = Pattern.compile(REGEX);
        var matcher = pattern.matcher(dnsDomainName);
        if (!matcher.find()) {
            return builder.error(INVALID_DNS_DOMAIN_NAME_ERROR).build();
        }
        return builder.build();
    }
}
