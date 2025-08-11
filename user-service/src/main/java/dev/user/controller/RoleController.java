package dev.user.controller;

import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.user.service.KeycloakRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер, в котором представлены методы для работы с ролями пользователей
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Методы для работы с ролями пользователей")
@SecurityRequirement(name = "Keycloak")
public class RoleController {
    private final KeycloakRoleService roleService;

    /**
     * Получение всех записей о ролях пользователей
     */
    @Operation(
            summary = "Получение всех записей о ролях пользователей",
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
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<RoleResponse>> getAll() {
        List<RoleResponse> responses = roleService.getAll();

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Обновление роли пользователю",
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
                    @Parameter(
                            name = "currentRole",
                            description = "Текущая роль",
                            in = ParameterIn.QUERY,
                            schema = @Schema(
                                    implementation = String.class
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "newRole",
                            description = "Новая роль",
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
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = GenreResponse.class
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
    @PutMapping("/user/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> updateRoleToUser(@PathVariable("id") String id,
                                                 @RequestParam String currentRole,
                                                 @RequestParam String newRole) {
        roleService.updateRoleToUser(id, currentRole, newRole);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
