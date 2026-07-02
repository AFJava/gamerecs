package com.af.gamerecs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/* Access point for Twitch client ID/client secret from application.properties */
@ConfigurationProperties(prefix = "twitch.api")
public record TwitchProperties(String client_id, String client_secret) {

}
