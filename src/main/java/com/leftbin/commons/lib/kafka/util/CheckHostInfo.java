package com.leftbin.commons.lib.kafka.util;

import com.leftbin.commons.proto.v1.kafka.KeyQueryMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckHostInfo {

    @Value(value = "${kafka.streams.host-name:localhost}")
    private String hostName;

    @Value(value = "${kafka.streams.port:8080}")
    private String port;

    public boolean isCurrentHostInKeyQueryMeta(KeyQueryMeta keyQueryMeta) {
        var hostNameInkafka = keyQueryMeta.getHostInfo().getHost();
        var portInkafka = keyQueryMeta.getHostInfo().getPort();
        return hostNameInkafka.equals(hostName) && portInkafka == Integer.parseInt(port);
    }

}
