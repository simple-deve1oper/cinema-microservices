package dev.user.service;

import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserRegistrationRequest;
import dev.library.domain.user.dto.UserRequest;
import dev.library.domain.user.dto.UserResponse;
import dev.user.mapper.UserMapper;
import dev.user.service.impl.KeycloakUserServiceImpl;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("resource")
public class KeycloakUserServiceImplTest {
    final RealmResource resource = Mockito.mock(RealmResource.class);
    final UserMapper mapper = new UserMapper();
    final KeycloakRoleService roleService = Mockito.mock(KeycloakRoleService.class);
    final KeycloakUserService service = new KeycloakUserServiceImpl(resource, mapper, roleService);
    final UsersResource usersResource = Mockito.mock(UsersResource.class);
    final UserResource userResource = Mockito.mock(UserResource.class);

    UUID uuidOneRole = UUID.randomUUID();
    UUID uuidTwoRole = UUID.randomUUID();
    UUID uuidOneUser = UUID.randomUUID();
    UUID uuidTwoUser = UUID.randomUUID();
    UUID uuidThreeUser = UUID.randomUUID();

    RoleResponse roleResponseOne;
    RoleResponse roleResponseTwo;

    UserRepresentation userRepresentationOne;
    UserRepresentation userRepresentationTwo;
    UserRepresentation userRepresentationThree;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorUserIdNotFound", "Пользователь с идентификатором %s не найден");
        ReflectionTestUtils.setField(service, "errorUserUsernameAlreadyExists", "Пользователь с именем пользователя %s уже существует");
        ReflectionTestUtils.setField(service, "errorUserUsernameNotFound", "Пользователя с username %s не найдено");
        ReflectionTestUtils.setField(service, "errorUserEmailAlreadyExists", "Пользователь с электронной почтой %s уже существует");
        ReflectionTestUtils.setField(service, "errorUserEmailVerifiedAlreadyExists", "У пользователя с идентификатором %s электронная почта уже подтверждена");

        roleResponseOne = new RoleResponse(uuidOneRole.toString(), "manager");
        roleResponseTwo = new RoleResponse(uuidTwoRole.toString(), "client");

        userRepresentationOne = new UserRepresentation();
        userRepresentationOne.setId(uuidOneUser.toString());
        userRepresentationOne.setUsername("phone1234");
        userRepresentationOne.setEmail("phone1234@gmail.com");
        userRepresentationOne.setEmailVerified(true);
        userRepresentationOne.setFirstName("John");
        userRepresentationOne.setLastName("Smith");
        userRepresentationOne.setAttributes(Map.of("birthDate", List.of("2000-01-01")));
        userRepresentationOne.setEnabled(true);

        userRepresentationTwo = new UserRepresentation();
        userRepresentationTwo.setId(uuidTwoUser.toString());
        userRepresentationTwo.setUsername("tiger1234");
        userRepresentationTwo.setEmail("tiger1234@gmail.com");
        userRepresentationTwo.setEmailVerified(true);
        userRepresentationTwo.setFirstName("Alice");
        userRepresentationTwo.setLastName("Smith");
        userRepresentationTwo.setAttributes(Map.of("birthDate", List.of("1994-01-01")));
        userRepresentationTwo.setEnabled(true);

        userRepresentationThree = new UserRepresentation();
        userRepresentationThree.setId(uuidThreeUser.toString());
        userRepresentationThree.setUsername("man4567");
        userRepresentationThree.setEmail("man4567@mail.com");
        userRepresentationThree.setEmailVerified(false);
        userRepresentationThree.setFirstName("Ivan");
        userRepresentationThree.setLastName("Ivanov");
        userRepresentationThree.setAttributes(Map.of("birthDate", List.of("1995-10-03")));
        userRepresentationThree.setEnabled(true);

        Mockito
                .when(resource.users())
                .thenReturn(usersResource);
        Mockito
                .when(resource.users().get(Mockito.anyString()))
                .thenReturn(userResource);
    }

    @Test
    void getAll_ok() {
        Mockito
                .when(usersResource.list())
                .thenReturn(List.of(userRepresentationOne, userRepresentationTwo));
        Mockito
                .when(roleService.getByUserId(Mockito.anyString()))
                .thenReturn(roleResponseTwo);

        List<UserResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());

        Mockito
                .verify(usersResource, Mockito.times(1))
                .list();
        Mockito
                .verify(roleService, Mockito.times(2))
                .getByUserId(Mockito.anyString());
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(usersResource.list())
                .thenReturn(Collections.emptyList());

        List<UserResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(usersResource, Mockito.times(1))
                .list();
        Mockito
                .verify(roleService, Mockito.times(0))
                .getByUserId(Mockito.anyString());
    }

    @Test
    void getById() {
        Mockito
                .when(userResource.toRepresentation())
                .thenReturn(userRepresentationOne);
        Mockito
                .when(roleService.getByUserId(Mockito.anyString()))
                .thenReturn(roleResponseOne);

        UserResponse response = service.getById(uuidOneUser.toString());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(uuidOneUser.toString(), response.id());
        Assertions.assertEquals("phone1234", response.username());
        Assertions.assertEquals("phone1234@gmail.com", response.email());
        Assertions.assertTrue(response.emailVerified());
        Assertions.assertEquals("John", response.firstName());
        Assertions.assertEquals("Smith", response.lastName());
        Assertions.assertEquals("2000-01-01", response.birthDate());
        Assertions.assertTrue(response.active());

        Mockito
                .verify(userResource, Mockito.times(1))
                .toRepresentation();
        Mockito
                .verify(roleService, Mockito.times(1))
                .getByUserId(Mockito.anyString());
    }

    @Test
    void create_ok() {
        Response response = Response.created(Mockito.mock()).build();

        Mockito
                .when(usersResource.searchByUsername(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Collections.emptyList())
                .thenReturn(List.of(userRepresentationThree));
        Mockito
                .when(usersResource.searchByEmail(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(usersResource.create(Mockito.any(UserRepresentation.class)))
                .thenReturn(response);
        Mockito
                .when(usersResource.get(Mockito.anyString()).toRepresentation())
                .thenReturn(userRepresentationThree);
        Mockito
                .doNothing()
                .when(roleService)
                .addRoleToUser(Mockito.anyString(), Mockito.anyString());


        UserRegistrationRequest request = new UserRegistrationRequest(
                "man4567",
                "1234567890",
                "man4567@mail.com",
                "Ivan",
                "Ivanov",
                LocalDate.of(1995, 10, 1)

        );
        int code = service.create(request);
        Assertions.assertEquals(201, code);

        Mockito
                .verify(usersResource, Mockito.times(2))
                .searchByUsername(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(usersResource, Mockito.times(1))
                .searchByEmail(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(usersResource, Mockito.times(1))
                .create(Mockito.any(UserRepresentation.class));
        Mockito
                .verify(usersResource, Mockito.times(2))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(1))
                .toRepresentation();
        Mockito
                .verify(resource.users().get(Mockito.anyString()), Mockito.times(1))
                .sendVerifyEmail();
    }

    @Test
    void create_entityAlreadyExistsException_username() {
        Mockito
                .when(usersResource.searchByUsername(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(userRepresentationOne));

        UserRegistrationRequest request = new UserRegistrationRequest(
                "phone1234",
                "1234567890",
                "phone1234@mail.com",
                "Anton",
                "Ivanov",
                LocalDate.of(1995, 10, 1)

        );
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Пользователь с именем пользователя phone1234 уже существует";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(usersResource, Mockito.times(1))
                .searchByUsername(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(usersResource, Mockito.times(0))
                .searchByEmail(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(usersResource, Mockito.times(0))
                .create(Mockito.any(UserRepresentation.class));
        Mockito
                .verify(usersResource, Mockito.times(0))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(0))
                .toRepresentation();
        Mockito
                .verify(userResource, Mockito.times(0))
                .sendVerifyEmail();
    }

    @Test
    void create_entityAlreadyExistsException_email() {
        Mockito
                .when(usersResource.searchByUsername(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(usersResource.searchByEmail(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(userRepresentationOne));

        UserRegistrationRequest request = new UserRegistrationRequest(
                "abc1234",
                "1234567890",
                "phone1234@mail.com",
                "Anton",
                "Ivanov",
                LocalDate.of(1995, 10, 1)

        );
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Пользователь с электронной почтой phone1234@mail.com уже существует";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(usersResource, Mockito.times(1))
                .searchByUsername(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(usersResource, Mockito.times(1))
                .searchByEmail(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(usersResource, Mockito.times(0))
                .create(Mockito.any(UserRepresentation.class));
        Mockito
                .verify(usersResource, Mockito.times(0))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(0))
                .toRepresentation();
        Mockito
                .verify(userResource, Mockito.times(0))
                .sendVerifyEmail();
    }

    @Test
    void update_ok() {
        UserResponse userResponse = new UserResponse(
                uuidOneUser.toString(),
                "phone1234",
                "abc@mail.com",
                false,
                "Oleg",
                "Black",
                "2000-01-01",
                roleResponseTwo,
                true
        );

        Mockito
                .when(userResource.toRepresentation())
                .thenReturn(userRepresentationOne);
        Mockito
                .when(usersResource.searchByEmail(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(Collections.emptyList());
        Mockito
                .doNothing()
                .when(userResource)
                .update(Mockito.any(UserRepresentation.class));

        Mockito
                .when(roleService.getByUserId(Mockito.anyString()))
                .thenReturn(roleResponseTwo);

        UserRequest request = new UserRequest(
                "abc@mail.com",
                "Oleg",
                "Black",
                LocalDate.of(2000, 1, 1)
        );
        UserResponse response = service.update(uuidOneUser.toString(), request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userResponse.id(), response.id());
        Assertions.assertEquals(userResponse.username(), response.username());
        Assertions.assertEquals(userResponse.email(), response.email());
        Assertions.assertEquals(userResponse.emailVerified(), response.emailVerified());
        Assertions.assertEquals(userResponse.firstName(), response.firstName());
        Assertions.assertEquals(userResponse.lastName(), response.lastName());
        Assertions.assertEquals(userResponse.birthDate(), response.birthDate());
        Assertions.assertEquals(userResponse.role().authority(), response.role().authority());
        Assertions.assertEquals(userResponse.active(), response.active());

        Mockito
                .verify(userResource, Mockito.times(1))
                .toRepresentation();
        Mockito
                .verify(usersResource, Mockito.times(1))
                .searchByEmail(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(userResource, Mockito.times(1))
                .update(Mockito.any(UserRepresentation.class));
        Mockito
                .verify(roleService, Mockito.times(1))
                .getByUserId(Mockito.anyString());
    }

    @Test
    void update_entityAlreadyExistsException_email() {
        Mockito
                .when(userResource.toRepresentation())
                .thenReturn(userRepresentationOne);
        Mockito
                .when(usersResource.searchByEmail(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(userRepresentationTwo));

        UserRequest request = new UserRequest(
                "tiger1234@gmail.com",
                "Oleg",
                "Black",
                LocalDate.of(2000, 1, 1)
        );
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.update(uuidOneUser.toString(), request)
                );
        var expectedMessage = "Пользователь с электронной почтой tiger1234@gmail.com уже существует";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(userResource, Mockito.times(1))
                .toRepresentation();
        Mockito
                .verify(usersResource, Mockito.times(1))
                .searchByEmail(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(userResource, Mockito.times(0))
                .update(Mockito.any(UserRepresentation.class));
        Mockito
                .verify(roleService, Mockito.times(0))
                .getByUserId(Mockito.anyString());
    }

    @Test
    void deleteById_ok() {
        Response response = Response.noContent().build();

        Mockito
                .when(resource.users().delete(Mockito.anyString()))
                .thenReturn(response);

        int code = service.deleteById(uuidOneUser.toString());
        Assertions.assertEquals(204, code);

        Mockito
                .verify(usersResource, Mockito.times(1))
                .delete(Mockito.anyString());
    }

    @Test
    void deleteById_entityNotFoundException() {
        Response response = Response.status(404).build();

        Mockito
                .when(usersResource.delete(Mockito.anyString()))
                .thenReturn(response);

        UUID id = UUID.randomUUID();
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.deleteById(id.toString())
                );
        var expectedMessage = "Пользователь с идентификатором %s не найден".formatted(id.toString());
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(usersResource, Mockito.times(1))
                .delete(Mockito.anyString());
    }

    @Test
    void sendVerificationEmail_ok() {
        Mockito
                .when(userResource.toRepresentation())
                .thenReturn(userRepresentationThree);
        Mockito
                .doNothing()
                .when(userResource)
                .sendVerifyEmail();

        service.sendVerificationEmail(uuidThreeUser.toString());

        Mockito
                .verify(usersResource, Mockito.times(1))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(1))
                .toRepresentation();
        Mockito
                .verify(userResource, Mockito.times(1))
                .sendVerifyEmail();
    }

    @Test
    void sendVerificationEmail_entityAlreadyExistsException() {
        Mockito
                .when(userResource.toRepresentation())
                .thenReturn(userRepresentationTwo);

        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.sendVerificationEmail(uuidTwoUser.toString())
                );
        var expectedMessage = "У пользователя с идентификатором %s электронная почта уже подтверждена"
                .formatted(uuidTwoUser.toString());
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(usersResource, Mockito.times(1))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(1))
                .toRepresentation();
    }

    @Test
    void forgotPassword() {
        Mockito
                .when(usersResource.searchByUsername(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(userRepresentationOne));
        Mockito
                .when(usersResource.get(Mockito.anyString()))
                .thenReturn(userResource);
        Mockito
                .doNothing()
                .when(userResource)
                .executeActionsEmail(Mockito.anyList());

        service.forgotPassword(uuidOneUser.toString());

        Mockito
                .verify(usersResource, Mockito.times(1))
                .searchByUsername(Mockito.anyString(), Mockito.anyBoolean());
        Mockito
                .verify(usersResource, Mockito.times(1))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(1))
                .executeActionsEmail(Mockito.anyList());
    }

    @Test
    void updateActivity() {
        Mockito
                .when(usersResource.get(Mockito.anyString()))
                .thenReturn(userResource);
        Mockito
                .when(userResource.toRepresentation())
                .thenReturn(userRepresentationTwo);
        Mockito
                .doNothing()
                .when(userResource)
                .update(Mockito.any(UserRepresentation.class));

        service.updateActivity(uuidTwoUser.toString());

        Mockito
                .verify(usersResource, Mockito.times(1))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(1))
                .toRepresentation();
        Mockito
                .verify(userResource, Mockito.times(1))
                .update(Mockito.any(UserRepresentation.class));
    }

    @Test
    void updatePassword() {
        Mockito
                .when(usersResource.get(Mockito.anyString()))
                .thenReturn(userResource);
        Mockito
                .doNothing()
                .when(userResource)
                .executeActionsEmail(Mockito.anyList());

        service.updatePassword(UUID.randomUUID().toString());

        Mockito
                .verify(usersResource, Mockito.times(1))
                .get(Mockito.anyString());
        Mockito
                .verify(userResource, Mockito.times(1))
                .executeActionsEmail(Mockito.anyList());
    }
}
