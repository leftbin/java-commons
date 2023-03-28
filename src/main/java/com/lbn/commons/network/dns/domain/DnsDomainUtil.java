package com.lbn.commons.network.dns.domain;

public class DnsDomainUtil {

    private DnsDomainUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getNormalizedName(String domainName) {
        return domainName.replaceAll("\\.", "-");
    }
}
