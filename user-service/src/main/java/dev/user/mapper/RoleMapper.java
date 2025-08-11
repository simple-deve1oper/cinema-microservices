package dev.user.mapper;

import dev.library.domain.user.dto.RoleResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link RoleRepresentation}
 */
@Component
public class RoleMapper {
    /**
     * Преобразование данных в {@link RoleResponse}
     * @param representation - объект типа {@link RoleRepresentation}
     */
    public RoleResponse toResponse(RoleRepresentation representation) {
        return new RoleResponse(
                representation.getId(),
                representation.getName()
        );
    }
}
