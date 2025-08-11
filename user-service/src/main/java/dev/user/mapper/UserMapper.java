package dev.user.mapper;

import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserRegistrationRequest;
import dev.library.domain.user.dto.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для преобразования данных типа {@link UserRepresentation}
 */
@Component
public class UserMapper {
    /**
     * Преобразование данных в {@link UserResponse}
     * @param representation - объект типа {@link UserRepresentation}
     * @param roleResponse - объект типа {@link RoleResponse}
     */
    public UserResponse toResponse(UserRepresentation representation, RoleResponse roleResponse) {
        return new UserResponse(
                representation.getId(),
                representation.getUsername(),
                representation.getEmail(),
                representation.isEmailVerified(),
                representation.getFirstName(),
                representation.getLastName(),
                representation.getAttributes().get("birthDate").getFirst(),
                roleResponse,
                representation.isEnabled()
        );
    }

    /**
     * Преобразование данных в {@link UserRepresentation}
     * @param request - объект типа {@link UserRegistrationRequest}
     */
    public UserRepresentation toRepresentation(UserRegistrationRequest request) {
        UserRepresentation representation = new UserRepresentation();
        representation.setUsername(request.username());
        representation.setEmail(request.email());
        representation.setFirstName(request.firstName());
        representation.setLastName(request.lastName());
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("birthDate", List.of(request.birthDate().toString()));
        representation.setAttributes(attributes);

        return representation;
    }
}
