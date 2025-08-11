package dev.library.test.dto.constant;

public enum GrantType {
    PASSWORD("password"), CLIENT_CREDENTIALS("client_credentials");

    private final String name;

    GrantType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}