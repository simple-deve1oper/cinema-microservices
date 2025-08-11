package dev.library.domain.user.dto;

import dev.library.domain.user.dto.constant.Authority;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для получения данных о роли пользователя
 * @param id - идентификатор
 * @param authority - перечисление типа {@link Authority}
 */
@Schema(
        name = "PlaceResponse",
        description = "DTO для получения данных о роли пользователя"
)
public record RoleResponse(
        @Schema(name = "id", description = "Идентификатор")
        String id,
        @Schema(name = "authority", description = "Роль пользователя")
        String authority
) {}
