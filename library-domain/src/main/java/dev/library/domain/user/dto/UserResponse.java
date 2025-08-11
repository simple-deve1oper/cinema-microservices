package dev.library.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @param id
 * @param username - username
 * @param email - электронная почта
 * @param firstName - имя
 * @param lastName - фамилия
 * @param birthDate - дата рождения
 * @param role - объект типа {@link RoleResponse}
 * @param active - активность
 */
@Schema(
        name = "UserResponse",
        description = "DTO для получения данных о пользователе"
)
public record UserResponse(
        @Schema(name = "id", description = "Идентификатор")
        String id,
        @Schema(name = "username", description = "Username")
        String username,
        @Schema(name = "email", description = "Электронная почта")
        String email,
        @Schema(name = "emailVerified", description = "Верификация электронной почты")
        boolean emailVerified,
        @Schema(name = "firstName", description = "Имя")
        String firstName,
        @Schema(name = "lastName", description = "Фамилия")
        String lastName,
        @Schema(name = "birthDate", description = "Дата рождения")
        String birthDate,
        @Schema(name = "role", description = "Роль")
        RoleResponse role,
        @Schema(name = "active", description = "Активность")
        boolean active
) {}
