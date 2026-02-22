package com.project.qa.helpers.user.engine;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Token {
    private String accessToken;
    private LocalDateTime tokenCreatedTime;
    private int tokenExpiresIn;
    private String idToken;

    public Token(String idToken) {
        this.tokenCreatedTime = LocalDateTime.now();
        this.idToken = idToken;
    }

    public Token(String accessToken, int tokenExpiresIn, String idToken) {
        this.accessToken = accessToken;
        this.tokenExpiresIn = tokenExpiresIn;
        this.tokenCreatedTime = LocalDateTime.now();
        this.idToken = idToken;
    }

    public boolean isValid() {
//        return LocalDateTime.now().isBefore(tokenCreatedTime.plusSeconds(tokenExpiresIn - 120)); as we do not have session expiration time
        return true;
    }
}
