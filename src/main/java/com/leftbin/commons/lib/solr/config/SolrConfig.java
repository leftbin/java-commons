package com.leftbin.commons.lib.solr.config;

import lombok.Data;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SolrConfig {

    @Value(value = "${solr.endpoint}/${solr.collection.resource}")
    private String solrResourceUrl;

    @Bean
    public SolrClient solrResourceClient() {
        return new HttpSolrClient.Builder(solrResourceUrl).build();
    }
}
