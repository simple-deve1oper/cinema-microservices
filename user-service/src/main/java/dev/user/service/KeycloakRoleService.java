package dev.user.service;

import dev.library.domain.user.dto.RoleResponse;

import java.util.List;

/**
 * Интерфейс для описания абстрактных методов сервиса для работы с ролями пользователей в Keycloak
 */
public interface KeycloakRoleService {
    /**
     * Получение всех записей о ролях пользователей
     */
    List<RoleResponse> getAll();

    /**
     * Получение записи роли пользователя по идентификатору пользователя
     * @param userId - идентификатор пользователя
     */
    RoleResponse getByUserId(String userId);

    /**
     * Добавление роли пользователю
     * @param userId - идентификатор пользователя
     * @param roleName - наименование роли
     */
    void addRoleToUser(String userId, String roleName);

    /**
     * Обновление роли пользователю
     * @param userId - идентификатор пользователя
     * @param currentRole - текущая роль
     * @param newRole - новая роль
     */
    void updateRoleToUser(String userId, String currentRole, String newRole);
}
