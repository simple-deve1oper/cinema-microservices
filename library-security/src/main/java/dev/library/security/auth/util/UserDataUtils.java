package dev.library.security.auth.util;

import dev.library.core.exception.AccessForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Вспомогательный класс для работы с данными пользователя
 */
public class UserDataUtils {
    private static final String ERROR_MESSAGE_EMAIL_VERIFICATION = "Пользователю %s необходимо подтвердить электронную почту для продолжения работы";

    /**
     * Верифицирована ли электронная почта
     * @param authentication - объект типа {@link Authentication}
     */
    public static boolean isEmailVerified(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return jwt.getClaim("email_verified");
    }

    /**
     * Проверка на верификацию электронной почты
     * @param authentication - объект типа {@link Authentication}
     */
    public static void checkEmailVerification(Authentication authentication) {
        if (!isEmailVerified(authentication)) {
            String errorMessage = ERROR_MESSAGE_EMAIL_VERIFICATION.formatted(authentication.getName());
            throw new AccessForbiddenException(errorMessage);
        }
    }

    /**
     * Получение объекта типа{@link Authentication}
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
