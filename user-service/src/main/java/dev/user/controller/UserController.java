package dev.user.controller;

import dev.library.core.exception.AccessForbiddenException;
import dev.library.core.exception.dto.ApiErrorResponse;
import dev.library.core.util.DataValidation;
import dev.library.domain.user.dto.UserRegistrationRequest;
import dev.library.domain.user.dto.UserRequest;
import dev.library.domain.user.dto.UserResponse;
import dev.library.security.auth.util.RoleUtils;
import dev.library.security.auth.util.UserDataUtils;
import dev.user.service.KeycloakUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер, в котором представлены методы для работы с пользователями
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Методы для работы с пользователями")
@SecurityRequirement(name = "Keycloak")
public class UserController {
    private final KeycloakUserService service;

    @Value("${errors.user.id.access-forbidden.client.view}")
    private String errorUserIdAccessForbiddenClientView;
    @Value("${errors.user.id.access-forbidden.client.update}")
    private String errorUserIdAccessForbiddenClientUpdate;
    @Value("${errors.user.id.access-forbidden.client.verification-email}")
    private String errorUserIdAccessForbiddenClientVerificationEmail;
    @Value("${errors.user.id.access-forbidden.update-password}")
    private String errorUserIdAccessForbiddenUpdatePassword;

    /**
     * Получение всех записей о пользователях
     */
    @Operation(
            summary = "Получение всех записей о пользователях",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = UserResponse.class
                                                    )
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    )
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> responses = service.getAll();

        return ResponseEntity.ok(responses);
    }

    /**
     * Получение записи о пользователе по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Получение записи о пользователе по идентификатору",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    ),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = UserResponse.class
                                                    )
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<UserResponse> getById(@PathVariable("id") String id) {
        Authentication authentication = UserDataUtils.getAuthentication();
        if (RoleUtils.checkRole(authentication, "client") && !authentication.getName().equals(id)) {
            throw new AccessForbiddenException(errorUserIdAccessForbiddenClientView);
        }
        UserResponse response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Создание новой записи о пользователе
     * @param request - объект типа {@link UserRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Создание новой записи о пользователе",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = UserRegistrationRequest.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(
                                                    implementation = ApiErrorResponse.class
                                            )
                                    )
                            }
                    )
            }
    )
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid UserRegistrationRequest request,
                                       @Parameter(hidden = true) BindingResult bindingResult) {
        DataValidation.checkValidation(bindingResult);
        int statusCode = service.create(request);

        return ResponseEntity.status(statusCode).build();
    }

    /**
     * Обновление существующей записи о пользователе
     * @param id - идентификатор
     * @param request - объект типа {@link UserRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Обновление существующей записи о пользователе",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = UserRequest.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = UserResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(
                                                    implementation = ApiErrorResponse.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ApiErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<UserResponse> update(@PathVariable("id") String id,
                                               @RequestBody @Valid UserRequest request,
                                               @Parameter(hidden = true) BindingResult bindingResult) {
        Authentication authentication = UserDataUtils.getAuthentication();
        if (RoleUtils.checkRole(authentication, "client") && !authentication.getName().equals(id)) {
            throw new AccessForbiddenException(errorUserIdAccessForbiddenClientUpdate);
        }
        DataValidation.checkValidation(bindingResult);
        UserResponse response = service.update(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаление записи о фильме по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Удаление записи о фильме по идентификатору",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ApiErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") String id) {
        int statusCode = service.deleteById(id);

        return ResponseEntity.status(statusCode).build();
    }

    /**
     * Отправка письма по электронной почте для подтверждения электронной почты
     * @param id - идентификатор
     */
    @Operation(
            summary = "Отправка письма по электронной почте для подтверждения электронной почты",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "No Content"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ApiErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @PatchMapping("/{id}/send-verify-email")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<Void> sendVerificationEmail(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (RoleUtils.checkRole(authentication, "client") && !authentication.getName().equals(id)) {
            throw new AccessForbiddenException(errorUserIdAccessForbiddenClientVerificationEmail);
        }
        service.sendVerificationEmail(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Сброс пароля и отправка письма на электронную почту
     * @param username - username
     */
    @Operation(
            summary = "Сброс пароля и отправка письма на электронную почту",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "Username",
                            in = ParameterIn.QUERY,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema()
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ApiErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @PutMapping("/reset-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String username) {
        service.forgotPassword(username);

        return ResponseEntity.ok().build();
    }

    /**
     * Обновление активности аккаунта
     * @param id - идентификатор
     */
    @Operation(
            summary = "Обновление активности аккаунта",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema()
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    )
            }
    )
    @PatchMapping("/{id}/activity")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<Void> updateActivity(@PathVariable String id) {
        service.updateActivity(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Обновление пароля пользователю
     * @param id - идентификатор
     */
    @Operation(
            summary = "Обновление пароля пользователю",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema()
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
                                    )
                            )
                    )
            }
    )
    @PutMapping("/{id}/update-password")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<Void> updatePassword(@PathVariable String id) {
        Authentication authentication = UserDataUtils.getAuthentication();
        if (!authentication.getName().equals(id)) {
            throw new AccessForbiddenException(errorUserIdAccessForbiddenUpdatePassword);
        }
        service.updatePassword(id);

        return ResponseEntity.ok().build();
    }
}
