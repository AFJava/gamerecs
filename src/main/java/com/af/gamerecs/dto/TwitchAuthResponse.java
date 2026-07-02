package com.af.gamerecs.dto;

//expires_in contains number of seconds until expiration
public record TwitchAuthResponse(String access_token, int expires_in, String token_type) {

}
