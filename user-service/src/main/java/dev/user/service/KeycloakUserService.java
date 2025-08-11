package dev.user.service;

import dev.library.domain.user.dto.UserRegistrationRequest;
import dev.library.domain.user.dto.UserRequest;
import dev.library.domain.user.dto.UserResponse;

import java.util.List;

/**
 * Интерфейс для описания абстрактных методов сервиса для работы с пользователями в Keycloak
 */
public interface KeycloakUserService {
    /**
     * Получение всех записей о пользователях
     */
    List<UserResponse> getAll();

    /**
     * Получение записи о пользователе по идентификатору
     * @param id - идентификатор
     */
    UserResponse getById(String id);

    /**
     * Создание новой записи о пользователе
     * @param request - объект типа {@link UserRequest}
     */
    int create(UserRegistrationRequest request);

    /**
     * Обновление существующей записи о пользователе
     * @param id - идентификатор
     * @param request - объект типа {@link UserRequest}
     */
    UserResponse update(String id, UserRequest request);

    /**
     * Удаление записи о фильме по идентификатору
     * @param id - идентификатор
     */
    int deleteById(String id);

    /**
     * Отправка письма по электронной почте для подтверждения электронной почты
     * @param id - идентификатор
     */
    void sendVerificationEmail(String id);

    /**
     * Сброс пароля и отправка письма на электронную почту
     * @param username - username
     */
    void forgotPassword(String username);

    /**
     * Обновление активности аккаунта
     * @param id - идентификатор
     */
    void updateActivity(String id);

    /**
     * Обновление пароля пользователю
     * @param id - идентификатор
     */
    void updatePassword(String id);
}
