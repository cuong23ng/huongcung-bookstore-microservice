package com.huongcung.inventoryservice.search.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Solr configuration properties
 * Loads from application.yml under 'solr' prefix
 */
@Configuration
@ConfigurationProperties(prefix = "solr")
@Getter
@Setter
@Slf4j
public class SolrConfig {

    private String host;

    private int port;

    private String core;
    
    /**
     * Connection timeout in milliseconds (default: 5000)
     */
    private int connectionTimeout = 5000;
    
    /**
     * Socket timeout in milliseconds (default: 10000)
     */
    private int socketTimeout = 10000;
    
    /**
     * Get the base URL for Solr
     * @return Base URL (e.g., http://localhost:8983/solr)
     */
    public String getBaseUrl() {
        return String.format("http://%s:%d/solr", host, port);
    }
    
    /**
     * Get the core URL for Solr
     * @return Core URL (e.g., http://localhost:8983/solr/books)
     */
    public String getCoreUrl() {
        return String.format("%s/%s", getBaseUrl(), core);
    }
}

