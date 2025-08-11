package dev.session.controller;

import dev.library.core.exception.dto.ApiErrorResponse;
import dev.library.core.util.DataValidation;
import dev.library.core.util.ResponseUtils;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.session.dto.SessionSearchRequest;
import dev.library.domain.session.dto.SessionRequest;
import dev.library.domain.session.dto.SessionResponse;
import dev.session.service.SessionService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Контроллер, в котором представлены методы для работы с сеансами
 */
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Методы для работы с сеансами")
@SecurityRequirement(name = "Keycloak")
public class SessionController {
    private final SessionService service;

    /**
     * Получение всех записей о сеансах
     * @param searchRequest - объект типа {@link SessionSearchRequest}
     */
    @Operation(
            summary = "Получение всех записей о сеансах",
            parameters = {
                    @Parameter(
                            name = "searchRequest",
                            description = "Объект для фильтрации поиска сеансов",
                            in = ParameterIn.QUERY,
                            schema = @Schema(
                                    implementation = SessionSearchRequest.class
                            )
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
                                                            implementation = SessionResponse.class
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
    public ResponseEntity<List<SessionResponse>> getAll(@ModelAttribute SessionSearchRequest searchRequest) {
        List<SessionResponse> responses = service.getAll(searchRequest);

        return ResponseEntity.ok(responses);
    }

    /**
     * Получение записи о сеансе по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Получение записи о сеансе по идентификатору",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = Long.class
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
                                                            implementation = SessionResponse.class
                                                    )
                                            )
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
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getById(@PathVariable Long id) {
        SessionResponse response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Создание новой записи о сеансе
     * @param request - объект типа {@link SessionRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Создание новой записи о сеансе",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = SessionRequest.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(
                                                    implementation = MovieResponse.class
                                            )
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
                                    schema = @Schema(
                                            implementation = ApiErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ApiErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<SessionResponse> create(@RequestBody @Valid SessionRequest request,
                                                  @Parameter(hidden = true) BindingResult bindingResult) {
        DataValidation.checkValidation(bindingResult);
        SessionResponse response = service.create(request);
        String entityLocation = ResponseUtils.createEntityLocation(response.id());

        return ResponseEntity.created(URI.create(entityLocation)).body(response);
    }

    /**
     * Обновление существующей записи о сеансе
     * @param id - идентификатор
     * @param request - объект типа {@link SessionRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Обновление существующей записи о сеансе",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = Long.class
                            ),
                            required = true
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = SessionRequest.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(
                                                    implementation = MovieResponse.class
                                            )
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
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<SessionResponse> update(@PathVariable Long id,
                                                  @RequestBody @Valid SessionRequest request,
                                                  @Parameter(hidden = true) BindingResult bindingResult) {
        DataValidation.checkValidation(bindingResult);
        SessionResponse response = service.update(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаление записи о сеансе по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Удаление записи о сеансе по идентификатору",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = Long.class
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
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
