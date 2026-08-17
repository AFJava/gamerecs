package com.af.gamerecs.exception;

public class IgdbRateLimitException extends RuntimeException {
    public IgdbRateLimitException() {
        super("IGDB rate limit exceeded");
    }

    public IgdbRateLimitException(Throwable cause) {
        super("IGDB rate limit exceeded", cause);
    }
}
