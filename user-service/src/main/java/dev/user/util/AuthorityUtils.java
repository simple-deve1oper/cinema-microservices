package dev.user.util;

import dev.library.core.exception.ServerException;
import dev.library.domain.user.dto.constant.Authority;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.Arrays;
import java.util.List;

/**
 * Вспомогательный класс для работы с ролями пользователя в Keycloak
 */
public class AuthorityUtils {
    /**
     * Получение роли после фильтрации
     * @param representations - список объектов типа {@link RoleRepresentation}
     */
    public static RoleRepresentation getRoleAfterFilterByAuthority(List<RoleRepresentation> representations) {
        List<String> authorities = Arrays.stream(Authority.values()).map(Authority::getValue).toList();
        return representations.stream()
                .filter(representation -> authorities.contains(representation.getName()))
                .findFirst()
                .orElseThrow(() -> new ServerException("Произошла ошибка фильтрации ролей пользователя"));
    }
}
