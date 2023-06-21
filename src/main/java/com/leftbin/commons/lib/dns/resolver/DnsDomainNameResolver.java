package com.leftbin.commons.lib.dns.resolver;

import org.apache.commons.lang3.StringUtils;
import org.xbill.DNS.Record;
import org.xbill.DNS.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DnsDomainNameResolver {

    private static final String GOOGLE_DNS_RESOLVER = "8.8.8.8";

    /**
     * @param name dns record name
     * @param type org.xbill.DNS.Type
     * @return list of dns record values
     */
    public static List<String> resolve(String name, int type) throws UnknownHostException, ExecutionException, InterruptedException {
        var resp = new ArrayList<String>();
        Record queryRecord = null;
        try {
            queryRecord = org.xbill.DNS.Record.newRecord(Name.fromString(
                StringUtils.appendIfMissing(name, ".")), type, DClass.IN);
        } catch (TextParseException e) {
            throw new RuntimeException(e);
        }
        Message queryMessage = Message.newQuery(queryRecord);
        Resolver r = new SimpleResolver(GOOGLE_DNS_RESOLVER);
        r.sendAsync(queryMessage)
            .whenComplete(
                (answer, ex) -> {
                    if (ex != null) {
                        //this should be included in richer error model
                        ex.printStackTrace();
                        throw new RuntimeException(
                            String.format("failed to query nameservers with error %s", ex.getMessage()));

                    }
                    answer.getSectionRRsets(Section.ANSWER).get(0).rrs().forEach(
                        record -> resp.add(record.rdataToString()));
                })
            .toCompletableFuture()
            .get();
        return resp;
    }

    /**
     * @param name           dns record name
     * @param type           org.xbill.DNS.Type
     * @param expectedValues expected resolved values
     * @return true if expected resolved values match resolved values
     * @throws UnknownHostException
     */
    public static boolean resolve(String name, int type, List<String> expectedValues) throws
        UnknownHostException, ExecutionException, InterruptedException {
        return new HashSet<>(resolve(name, type)).containsAll(expectedValues);
    }
}
