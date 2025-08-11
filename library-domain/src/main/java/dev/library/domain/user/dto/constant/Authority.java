package dev.library.domain.user.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для ролей пользователей
 */
@Schema(
        name = "Authority",
        description = "Перечисление для ролей пользователей"
)
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
