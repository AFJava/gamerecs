package com.af.gamerecs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/* Store API key from env varibales and URL to use for all RAWG API requests */
@ConfigurationProperties(prefix = "rawg.api")
public record RawgProperties(String key, String baseUrl) {}