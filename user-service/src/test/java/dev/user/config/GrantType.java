package dev.user.config;

public enum GrantType {
    PASSWORD("password"), CLIENT_CREDENTIALS("client_credentials");

    private final String value;

    GrantType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}