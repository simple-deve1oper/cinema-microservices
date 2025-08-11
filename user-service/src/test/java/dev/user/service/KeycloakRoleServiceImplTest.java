package dev.user.service;

import dev.library.core.exception.ServerException;
import dev.library.domain.user.dto.RoleResponse;
import dev.user.mapper.RoleMapper;
import dev.user.service.impl.KeycloakRoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class KeycloakRoleServiceImplTest {
    final RealmResource resource = Mockito.mock(RealmResource.class);
    final RoleMapper mapper = new RoleMapper();
    final KeycloakRoleService service = new KeycloakRoleServiceImpl(resource, mapper);
    final RolesResource rolesResource = Mockito.mock(RolesResource.class);
    final RoleResource roleResource = Mockito.mock(RoleResource.class);
    final UsersResource usersResource = Mockito.mock(UsersResource.class);
    final UserResource userResource = Mockito.mock(UserResource.class);
    final RoleMappingResource roleMappingResource = Mockito.mock(RoleMappingResource.class);
    final RoleScopeResource roleScopeResource = Mockito.mock(RoleScopeResource.class);

    RoleRepresentation roleRepresentationOne;
    RoleRepresentation roleRepresentationTwo;

    RoleResponse roleResponseOne;
    RoleResponse roleResponseTwo;

    UUID uuidOne = UUID.randomUUID();
    UUID uuidTwo = UUID.randomUUID();

    @BeforeEach
    void init() {
        roleRepresentationOne = new RoleRepresentation();
        roleRepresentationOne.setId(uuidOne.toString());
        roleRepresentationOne.setName("manager");
        roleRepresentationTwo = new RoleRepresentation();
        roleRepresentationTwo.setId(uuidOne.toString());
        roleRepresentationTwo.setName("client");

        roleResponseOne = new RoleResponse(uuidOne.toString(), "manager");
        roleResponseTwo = new RoleResponse(uuidTwo.toString(), "client");

        Mockito
                .when(resource.roles())
                .thenReturn(rolesResource);
        Mockito
                .when(resource.roles().get(Mockito.anyString()))
                .thenReturn(roleResource);
        Mockito
                .when(resource.users())
                .thenReturn(usersResource);
        Mockito
                .when(resource.users().get(Mockito.anyString()))
                .thenReturn(userResource);
        Mockito
                .when(resource.users().get(Mockito.anyString()).roles())
                .thenReturn(roleMappingResource);
        Mockito
                .when(resource.users().get(Mockito.anyString()).roles().realmLevel())
                .thenReturn(roleScopeResource);
    }

    @Test
    void getAll_ok() {
        Mockito
                .when(rolesResource.list())
                .thenReturn(List.of(roleRepresentationOne, roleRepresentationTwo));

        List<RoleResponse> roleResponses = service.getAll();
        Assertions.assertNotNull(roleResponses);
        Assertions.assertFalse(roleResponses.isEmpty());
        Assertions.assertEquals(2, roleResponses.size());

        Mockito
                .verify(rolesResource, Mockito.times(1))
                .list();
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(rolesResource.list())
                .thenReturn(Collections.emptyList());

        List<RoleResponse> roleResponses = service.getAll();
        Assertions.assertNotNull(roleResponses);
        Assertions.assertTrue(roleResponses.isEmpty());

        Mockito
                .verify(rolesResource, Mockito.times(1))
                .list();
    }

    @Test
    void getByUserId_ok() {
        Mockito
                .when(roleScopeResource.listAll())
                .thenReturn(List.of(roleRepresentationTwo));

        RoleResponse roleResponse = service.getByUserId(uuidOne.toString());
        Assertions.assertNotNull(roleResponse);
        Assertions.assertEquals(uuidOne.toString(), roleResponse.id());
        Assertions.assertEquals("client", roleResponse.authority());

        Mockito
                .verify(roleScopeResource, Mockito.times(1))
                .listAll();
    }

    @Test
    void getByUserId_serverException() {
        Mockito
                .when(roleScopeResource.listAll())
                .thenReturn(Collections.emptyList());

        ServerException exception = Assertions
                .assertThrows(
                        ServerException.class,
                        () -> service.getByUserId(UUID.randomUUID().toString())
                );
        var expectedMessage = "Произошла ошибка фильтрации ролей пользователя";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(roleScopeResource, Mockito.times(1))
                .listAll();
    }

    @Test
    void addRoleToUser() {
        Mockito
                .when(roleResource.toRepresentation())
                .thenReturn(roleRepresentationOne);
        Mockito
                .doNothing()
                .when(roleScopeResource)
                .add(Mockito.anyList());

        service.addRoleToUser(UUID.randomUUID().toString(), "manager");

        Mockito
                .verify(roleResource, Mockito.times(1))
                .toRepresentation();
        Mockito
                .verify(roleScopeResource, Mockito.times(1))
                .add(Mockito.anyList());
    }

    @Test
    void updateRoleToUser() {
        Mockito
                .when(roleResource.toRepresentation())
                .thenReturn(roleRepresentationOne)
            .thenReturn(roleRepresentationTwo);
        Mockito
                .doNothing()
                .when(roleScopeResource)
                .remove(Mockito.anyList());
        Mockito
                .doNothing()
                .when(roleScopeResource)
                .add(Mockito.anyList());

        service.updateRoleToUser(UUID.randomUUID().toString(), "client", "manager");

        Mockito
                .verify(roleResource, Mockito.times(2))
                .toRepresentation();
        Mockito
                .verify(roleScopeResource, Mockito.times(1))
                .add(Mockito.anyList());
        Mockito
                .verify(roleScopeResource, Mockito.times(1))
                .remove(Mockito.anyList());
    }
}
