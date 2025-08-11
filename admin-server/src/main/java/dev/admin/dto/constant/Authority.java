package dev.admin.dto.constant;

/**
 * Перечисление для ролей пользователей
 */
public enum Authority {
    CLIENT("client"), MANAGER("manager"), ADMIN("admin");

    private final String value;

    Authority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
