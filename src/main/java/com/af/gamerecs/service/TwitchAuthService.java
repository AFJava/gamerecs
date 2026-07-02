package com.af.gamerecs.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.af.gamerecs.config.TwitchProperties;
import com.af.gamerecs.dto.TwitchAuthResponse;

@Service
public class TwitchAuthService {
    private final TwitchProperties twitchProperties;
    @Qualifier("twitchWebClient") private final WebClient twitchWebClient;
    private String accessToken;
    private Instant expiration;

    public TwitchAuthService(TwitchProperties twitchProperties, WebClient twitchWebClient) {
        this.twitchProperties = twitchProperties;
        this.twitchWebClient = twitchWebClient;
    }

    public String getAccessToken() {
        if(accessToken == null || Instant.now().isAfter(expiration)) {
            getNewToken();
        }

        return accessToken;
    }

    public void getNewToken() {
        TwitchAuthResponse response = twitchWebClient.post()
            .uri(uriBuilder -> uriBuilder.path("/oauth2/token")
                .queryParam("client_id", twitchProperties.client_id())
                .queryParam("client_secret", twitchProperties.client_secret())
                .queryParam("grant_type", "client_credentials")
                .build()
            )
            .retrieve()
            .bodyToMono(TwitchAuthResponse.class) //TODO create TwitchAuthResponse
            .block();
        
        accessToken = response.access_token();
        expiration = Instant.now().plusSeconds(response.expires_in());
    }
}
