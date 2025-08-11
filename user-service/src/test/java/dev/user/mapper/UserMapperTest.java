package dev.user.mapper;

import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserRegistrationRequest;
import dev.library.domain.user.dto.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserMapperTest {
    final UserMapper mapper = new UserMapper();

    @Test
    void toResponse() {
        String id = UUID.randomUUID().toString();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername("pavel007");
        userRepresentation.setEmail("pavel007@example.com");
        userRepresentation.setEmailVerified(true);
        userRepresentation.setFirstName("Pavel");
        userRepresentation.setLastName("Belov");
        userRepresentation.setAttributes(Map.of("birthDate", List.of("2001-12-12")));
        userRepresentation.setEnabled(true);

        String roleId = UUID.randomUUID().toString();
        RoleResponse roleResponse = new RoleResponse(roleId, "client");

        UserResponse response = mapper.toResponse(userRepresentation, roleResponse);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(id, response.id());
        Assertions.assertEquals("pavel007", response.username());
        Assertions.assertEquals("pavel007@example.com", response.email());
        Assertions.assertTrue(response.emailVerified());
        Assertions.assertEquals("Pavel", response.firstName());
        Assertions.assertEquals("Belov", response.lastName());
        Assertions.assertEquals("2001-12-12", response.birthDate());
        Assertions.assertTrue(true, response.email());
        Assertions.assertEquals(roleId, response.role().id());
        Assertions.assertEquals("client", response.role().authority());
    }

    @Test
    void toRepresentation() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test1234",
                "1234",
                "test1234@example.com",
                "Test",
                "Ten",
                LocalDate.of(1999, 1, 1)
        );

        UserRepresentation representation = mapper.toRepresentation(request);
        Assertions.assertNotNull(representation);
        Assertions.assertEquals("test1234", representation.getUsername());
        Assertions.assertEquals("test1234@example.com", representation.getEmail());
        Assertions.assertEquals("Test", representation.getFirstName());
        Assertions.assertEquals("Ten", representation.getLastName());
        Assertions.assertEquals("1999-01-01", representation.getAttributes().get("birthDate").getFirst());
    }
}
