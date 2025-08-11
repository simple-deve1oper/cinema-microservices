package dev.library.security.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Вспомогательный класс для работы с ролями пользователя
 */
public class RoleUtils {
    /**
     * Проверка роли
     * @param authentication - объект типа {@link Authentication}
     * @param role - роль пользователя
     */
    public static boolean checkRole(Authentication authentication, String role) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_%s".formatted(role)));
    }
}
