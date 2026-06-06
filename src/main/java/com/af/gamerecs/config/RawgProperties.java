package com.af.gamerecs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rawg.api")
public record RawgProperties(String key, String baseUrl) {}