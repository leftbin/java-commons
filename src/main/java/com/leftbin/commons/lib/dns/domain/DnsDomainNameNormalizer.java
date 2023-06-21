package com.leftbin.commons.lib.dns.domain;

import org.apache.commons.lang3.StringUtils;

/**
 * A utility class for normalizing DNS domain names.
 */
public class DnsDomainNameNormalizer {

    /**
     * Normalizes a DNS domain name by replacing dots with hyphens and converting it to lowercase.
     *
     * @param dnsDomainName the DNS domain name to normalize
     * @return the normalized DNS domain name
     */
    public static String normalize(String dnsDomainName) {
        return StringUtils.replaceChars(dnsDomainName, ".", "-").toLowerCase();
    }
}
