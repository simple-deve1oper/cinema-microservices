package dev.user.service.impl;

import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.constant.Authority;
import dev.user.mapper.RoleMapper;
import dev.user.service.KeycloakRoleService;
import dev.user.util.AuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Сервис, реализующий интерфейс {@link KeycloakRoleService}
 */
@Service
@RequiredArgsConstructor
public class KeycloakRoleServiceImpl implements KeycloakRoleService {
    private final RealmResource resource;
    private final RoleMapper mapper;

    @Override
    public List<RoleResponse> getAll() {
        List<RoleRepresentation> roleRepresentations = resource.roles().list();
        List<String> authorities = Arrays.stream(Authority.values()).map(Authority::getValue).toList();

        return roleRepresentations.stream()
                .filter(representation -> authorities.contains(representation.getName()))
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public RoleResponse getByUserId(String userId) {
        List<RoleRepresentation> representations = getRoleScopeResourceByUserId(userId).listAll();
        RoleRepresentation representation = AuthorityUtils.getRoleAfterFilterByAuthority(representations);

        return mapper.toResponse(representation);
    }

    @Override
    public void addRoleToUser(String userId, String roleName) {
        RoleRepresentation roleRepresentation = getRoleByName(roleName);
        getRoleScopeResourceByUserId(userId).add(List.of(roleRepresentation));
    }

    @Override
    public void updateRoleToUser(String userId, String currentRole, String newRole) {
        RoleScopeResource roleScopeResource = getRoleScopeResourceByUserId(userId);
        roleScopeResource.remove(List.of(getRoleByName(currentRole)));
        roleScopeResource.add(List.of(getRoleByName(newRole)));
    }

    /**
     * Получение объекта типа {@link RoleScopeResource} по идентификатору пользователя
     * @param userId - идентификатор пользователя
     */
    private RoleScopeResource getRoleScopeResourceByUserId(String userId) {
        return resource.users().get(userId)
                .roles()
                .realmLevel();
    }

    /**
     * Получение объекта типа {@link RoleRepresentation} по наименованию роли
     * @param roleName - наименование роли
     */
    private RoleRepresentation getRoleByName(String roleName) {
        return resource.roles()
                .get(roleName)
                .toRepresentation();
    }
}
