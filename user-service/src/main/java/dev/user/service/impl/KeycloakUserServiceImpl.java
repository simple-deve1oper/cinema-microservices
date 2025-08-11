package dev.user.service.impl;

import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserRegistrationRequest;
import dev.library.domain.user.dto.UserRequest;
import dev.library.domain.user.dto.UserResponse;
import dev.library.domain.user.dto.constant.Action;
import dev.library.domain.user.dto.constant.Authority;
import dev.user.mapper.UserMapper;
import dev.user.service.KeycloakRoleService;
import dev.user.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Сервис, реализующий интерфейс {@link KeycloakUserService}
 */
@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService {
    private final RealmResource resource;
    private final UserMapper mapper;
    private final KeycloakRoleService roleService;

    @Value("${errors.user.id.not-found}")
    private String errorUserIdNotFound;
    @Value("${errors.user.username.already-exists}")
    private String errorUserUsernameAlreadyExists;
    @Value("${errors.user.username.not-found}")
    private String errorUserUsernameNotFound;
    @Value("${errors.user.email.already-exists}")
    private String errorUserEmailAlreadyExists;
    @Value("${errors.user.email-verified.already-exists}")
    private String errorUserEmailVerifiedAlreadyExists;

    @Override
    public List<UserResponse> getAll() {
        List<UserRepresentation> userRepresentations = getUsersResource().list();

        return userRepresentations.stream()
                .map(representation -> {
                    RoleResponse roleResponse = roleService.getByUserId(representation.getId());
                    return mapper.toResponse(representation, roleResponse);
                })
                .toList();
    }

    @Override
    public UserResponse getById(String id) {
        UserRepresentation representation = getUserResourceById(id).toRepresentation();
        RoleResponse roleResponse = roleService.getByUserId(id);

        return mapper.toResponse(representation, roleResponse);
    }

    @Override
    public int create(UserRegistrationRequest request) {
        checkUsernameAndEmailWhenCreating(request.username(), request.email());
        UserRepresentation representation = mapper.toRepresentation(request);
        representation.setEmailVerified(Boolean.FALSE);
        representation.setEnabled(Boolean.TRUE);

        List<CredentialRepresentation> credentialRepresentations = new ArrayList<>();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(request.password());
        credentialRepresentations.add(credentialRepresentation);
        representation.setCredentials(credentialRepresentations);

        int statusCode;
        try (Response response = getUsersResource().create(representation)) {
            statusCode = response.getStatus();
            if (statusCode != 201) {
                return statusCode;
            }
            representation = findUserRepresentationByUsername(request.username());
            roleService.addRoleToUser(representation.getId(), Authority.CLIENT.getValue());
            statusCode = response.getStatus();
        }
        sendVerificationEmail(representation.getId());

        return statusCode;
    }

    @Override
    public UserResponse update(String id, UserRequest request) {
        UserRepresentation representation = getUserResourceById(id).toRepresentation();
        String oldEmail = representation.getEmail();
        boolean resultUpdate = replaceData(representation, request);
        if (resultUpdate) {
            if (!oldEmail.equals(representation.getEmail())) {
                representation.setEmailVerified(Boolean.FALSE);
            }
            getUsersResource()
                    .get(id)
                    .update(representation);
        }

        return mapper.toResponse(representation, roleService.getByUserId(representation.getId()));
    }

    @Override
    public int deleteById(String id) {
        try (Response response = getUsersResource().delete(id)) {
            int status = response.getStatus();
            if (status == 404) {
                String errorMessage = errorUserIdNotFound.formatted(id);
                throw new EntityNotFoundException(errorMessage);
            }
            return status;
        }
    }

    @Override
    public void sendVerificationEmail(String id) {
        UserResource userResource = getUserResourceById(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        if (userRepresentation.isEmailVerified()) {
            String errorMessage = errorUserEmailVerifiedAlreadyExists.formatted(userRepresentation.getId());
            throw new EntityAlreadyExistsException(errorMessage);
        }
        userResource.sendVerifyEmail();
    }

    @Override
    public void forgotPassword(String username) {
        UserRepresentation representation = findUserRepresentationByUsername(username);
        UserResource userResource = getUserResourceById(representation.getId());
        userResource.executeActionsEmail(List.of(Action.UPDATE_PASSWORD.name()));
    }

    @Override
    public void updateActivity(String id) {
        UserResource userResource = getUserResourceById(id);
        UserRepresentation representation = userResource.toRepresentation();
        representation.setEnabled(!representation.isEnabled());
        userResource.update(representation);
    }

    @Override
    public void updatePassword(String id) {
        UserResource userResource = getUserResourceById(id);
        userResource.executeActionsEmail(List.of(Action.UPDATE_PASSWORD.name()));
    }

    /**
     * Получение объекта типа {@link UsersResource}
     */
    private UsersResource getUsersResource() {
        return resource.users();
    }

    /**
     * Получение объекта типа {@link UserResource} по идентификатору
     * @param id - идентификатор
     */
    private UserResource getUserResourceById(String id) {
        return resource.users().get(id);
    }

    /**
     * Проверка username на существование
     * @param username - username
     */
    private boolean checkUsername(String username) {
        return resource.users()
                .searchByUsername(username, true)
                .stream()
                .findFirst().isPresent();
    }

    /**
     * Проверка email на существование
     * @param email - email
     */
    private boolean checkEmail(String email) {
        return resource.users()
                .searchByEmail(email, true)
                .stream()
                .findFirst().isPresent();
    }

    /**
     * Проверка username и email пользователя при создании
     * @param username - username
     * @param email - email
     */
    private void checkUsernameAndEmailWhenCreating(String username, String email) {
        if (checkUsername(username)) {
            String errorMessage = errorUserUsernameAlreadyExists.formatted(username);
            throw new EntityAlreadyExistsException(errorMessage);
        }
        if (checkEmail(email)) {
            String errorMessage = errorUserEmailAlreadyExists.formatted(email);
            throw new EntityAlreadyExistsException(errorMessage);
        }
    }

    /**
     * Нахождение объекта типа {@link UserRepresentation} по username
     * @param username - username
     */
    private UserRepresentation findUserRepresentationByUsername(String username) {
        return resource.users()
                .searchByUsername(username, true)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(errorUserUsernameNotFound.formatted(username)));

    }

    /**
     * Замен данных о пользователе
     * @param representation - объект типа {@link UserRepresentation}
     * @param request - - объект типа {@link UserRequest}
     */
    private boolean replaceData(UserRepresentation representation, UserRequest request) {
        int count = 0;
        if (!representation.getFirstName().equals(request.firstName())) {
            representation.setFirstName(request.firstName());
            count = count + 1;
        }
        if (!representation.getLastName().equals(request.lastName())) {
            representation.setLastName(request.lastName());
            count = count + 1;
        }
        if (!representation.getEmail().equals(request.email())) {
            if (checkEmail(request.email())) {
                String errorMessage = errorUserEmailAlreadyExists.formatted(request.email());
                throw new EntityAlreadyExistsException(errorMessage);
            }
            representation.setEmail(request.email());
            representation.setEmailVerified(false);
            count = count + 1;
        }
        Map<String, List<String>> attributes = representation.getAttributes();
        String birthdateRequest = request.birthDate().toString();
        String birthdateRepresentation = attributes.get("birthDate").getFirst();
        if (!birthdateRequest.equals(birthdateRepresentation)) {
            attributes.put("birthDate", List.of(birthdateRequest));
            representation.setAttributes(attributes);
            count = count + 1;
        }

        return count > 0;
    }
}
