package com.af.gamerecs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
    WebClient IgdbWebClient() {
        return WebClient.builder()
            .baseUrl("https://api.igdb.com/v4/")
            .build();
    }
}
