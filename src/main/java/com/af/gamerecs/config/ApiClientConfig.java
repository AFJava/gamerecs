package com.af.gamerecs.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@EnableConfigurationProperties(TwitchProperties.class)
public class ApiClientConfig {
    @Bean
    WebClient twitchWebClient() {
        return WebClient.builder()
            .baseUrl("https://id.twitch.tv")
            .build();
    }

    @Bean
    WebClient igdbWebClient() {
        return WebClient.builder()
            .baseUrl("https://api.igdb.com/v4")
            .build();
    }
}
