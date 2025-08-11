package dev.user.mapper;

import dev.library.domain.user.dto.RoleResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.UUID;

public class RoleMapperTest {
    final RoleMapper roleMapper = new RoleMapper();

    @Test
    void toResponse() {
        String id = UUID.randomUUID().toString();

        RoleRepresentation representation = new RoleRepresentation();
        representation.setId(id);
        representation.setName("client");

        RoleResponse response = roleMapper.toResponse(representation);
        Assertions.assertEquals(id, response.id());
        Assertions.assertEquals("client", response.authority());
    }
}
