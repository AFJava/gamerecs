package com.af.gamerecs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/* Access point for RAWG key and base URL from application.properties */
@ConfigurationProperties(prefix = "rawg.api")
public record RawgProperties(String key, String baseUrl) {}