package dev.user.service;

import dev.library.domain.schedule.dto.TaskResponse;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.user.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    final KeycloakUserService userService = Mockito.mock(KeycloakUserService.class);
    final TaskService service = new TaskServiceImpl(userService);

    UUID uuidOneRole = UUID.randomUUID();
    UUID uuidTwoRole = UUID.randomUUID();
    UUID uuidOneUser = UUID.randomUUID();
    UUID uuidTwoUser = UUID.randomUUID();

    RoleResponse roleResponseOne;
    RoleResponse roleResponseTwo;

    UserResponse userResponseOne;
    UserResponse userResponseTwo;

    @BeforeEach
    void init() {
        roleResponseOne = new RoleResponse(uuidOneRole.toString(), "manager");
        roleResponseTwo = new RoleResponse(uuidTwoRole.toString(), "client");

        userResponseOne = new UserResponse(
                uuidOneUser.toString(),
                "phone1234",
                "phone1234@gmail.com",
                false,
                "John",
                "Smith",
                "2000-01-01",
                roleResponseOne,
                true
        );
        userResponseTwo = new UserResponse(
                uuidTwoUser.toString(),
                "tiger1234",
                "tiger1234@gmail.com",
                false,
                "Alice",
                "Smith",
                "1994-01-01",
                roleResponseTwo,
                false
        );
    }

    @Test
    void deactivateIfNotEmailVerified() {
        Mockito
                .when(userService.getById(Mockito.anyString()))
                .thenReturn(userResponseOne);
        Mockito
                .doNothing()
                .when(userService)
                .updateActivity(Mockito.anyString());

        service.deactivateIfNotEmailVerified(uuidOneUser.toString());

        Mockito
                .verify(userService, Mockito.times(1))
                .getById(Mockito.anyString());
        Mockito
                .verify(userService, Mockito.times(1))
                .updateActivity(Mockito.anyString());
    }

    @Test
    void deleteRecordsIfNotActive() {
        Mockito
                .when(userService.getAll())
                .thenReturn(List.of(userResponseOne, userResponseTwo));
//        List<UserResponse> userResponses = Arrays.asList(userResponseOne, userResponseTwo);
//        Mockito
//                .when(userService.getAll())
//                .thenReturn(userResponses);
        Mockito
                .when(userService.deleteById(Mockito.anyString()))
                .thenReturn(200);

        TaskResponse response = new TaskResponse(Map.of("userState", "DELETE_INACTIVE"));
        service.deleteRecordsIfNotActive(response);

        Mockito
                .verify(userService, Mockito.times(1))
                .getAll();
        Mockito
                .verify(userService, Mockito.times(1))
                .deleteById(Mockito.anyString());
    }
}
