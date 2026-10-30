package edu.fscj.cen3024c.auth;


public class LoginResponse {
    private String token;

    private String tokenType;

    private String expiresIn;

    public LoginResponse(String token, String tokenType, String expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }
}
