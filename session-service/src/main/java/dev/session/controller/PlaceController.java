package dev.session.controller;

import dev.library.core.exception.dto.ApiErrorResponse;
import dev.library.core.util.DataValidation;
import dev.library.core.util.ResponseUtils;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.session.dto.PlaceRequest;
import dev.library.domain.session.dto.PlaceResponse;
import dev.session.service.PlaceService;
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
import java.util.Set;

/**
 * Контроллер, в котором представлены методы для работы с местами сеансов
 */
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Методы для работы с местами сеансов")
@SecurityRequirement(name = "Keycloak")
public class PlaceController {
    private final PlaceService service;

    /**
     * Получение всех записей о местах сеансов
     */
    @Operation(
            summary = "Получение всех записей о местах сеансов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = PlaceResponse.class
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
    public ResponseEntity<List<PlaceResponse>> getAll() {
        List<PlaceResponse> responses = service.getAll();

        return ResponseEntity.ok(responses);
    }

    /**
     * Получение записей мест сеанса по идентификатору сеанса
     * @param sessionId - идентификатор сеанса
     */
    @Operation(
            summary = "Получение записей мест сеанса по идентификатору сеанса",
            parameters = {
                    @Parameter(
                            name = "session-id",
                            description = "Идентификатор сеанса",
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
                                                            implementation = PlaceResponse.class
                                                    )
                                            )
                                    )
                            }
                    )
            }
    )
    @GetMapping("/session/{session-id}")
    public ResponseEntity<List<PlaceResponse>> getAllBySession_Id(@PathVariable("session-id") Long sessionId) {
        List<PlaceResponse> responses = service.getAllBySession_Id(sessionId);

        return ResponseEntity.ok(responses);
    }

    /**
     * Получение записей мест по переданному списку идентификаторов
     * @param ids - список идентификаторов мест
     */
    @Operation(
            summary = "Получение записей мест по переданному списку идентификаторов",
            parameters = {
                    @Parameter(
                            name = "values",
                            description = "Список идентификаторов мест",
                            in = ParameterIn.QUERY,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Long.class
                                    )
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
                                                            implementation = PlaceResponse.class
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
                    )
            }
    )
    @GetMapping("/search/ids")
    public ResponseEntity<List<PlaceResponse>> getAllByIds(@RequestParam(value = "values") Set<Long> ids) {
        List<PlaceResponse> responses = service.getAllByIds(ids);

        return ResponseEntity.ok(responses);
    }

    /**
     * Получение записи о месте сеанса по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Получение записи о месте сеанса по идентификатору",
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
                                                            implementation = MovieResponse.class
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
    public ResponseEntity<PlaceResponse> getById(@PathVariable Long id) {
        PlaceResponse response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Получение первого идентификатора места сеанса, который равен переданному идентификатору сеанса и доступности из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     * @param available - доступность
     */
    @Operation(
            summary = "Получение первого идентификатора места сеанса, который равен переданному идентификатору сеанса и доступности из списка идентификаторов мест",
            parameters = {
                    @Parameter(
                            name = "session-id",
                            description = "Идентификатор сеанса",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = Long.class
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "values",
                            description = "Список идентификаторов мест",
                            in = ParameterIn.QUERY,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Long.class
                                    )
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "available",
                            description = "Доступность",
                            in = ParameterIn.PATH,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Boolean.class
                                    )
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
                                            schema = @Schema(
                                                    implementation = Long.class
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
                    )
            }
    )
    @GetMapping("/search/session/{session-id}/ids")
    public ResponseEntity<Long> getPlaceBySessionIdAndIdsAndAvailable(@PathVariable("session-id") Long sessionId,
                                                                       @RequestParam(value = "values") Set<Long> ids,
                                                                       @RequestParam Boolean available) {
        return ResponseEntity.ok(service.getPlaceBySessionIdAndIdsAndAvailable(sessionId, ids, available));
    }

    /**
     * Получение первого идентификатора места сеанса, который не равен переданному идентификатору сеанса из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     */
    @Operation(
            summary = "Получение первого идентификатора места сеанса, который не равен переданному идентификатору сеанса из списка идентификаторов мест",
            parameters = {
                    @Parameter(
                            name = "session-id",
                            description = "Идентификатор сеанса",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = Long.class
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "values",
                            description = "Список идентификаторов мест",
                            in = ParameterIn.QUERY,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Long.class
                                    )
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
                                            schema = @Schema(
                                                    implementation = PlaceResponse.class
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
                    )
            }
    )
    @GetMapping("/search/session-not-equals/{session-id}/ids")
    public ResponseEntity<Long> getPlaceNotEqualsSessionBySessionIdAndIds(@PathVariable("session-id") Long sessionId,
                                                                          @RequestParam(value = "values") Set<Long> ids) {
        return ResponseEntity.ok(service.getPlaceNotEqualsSessionBySessionIdAndIds(sessionId, ids));
    }

    /**
     * Создание новой записи о месте сеанса
     * @param request - объект типа {@link PlaceRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Создание новой записи о месте сеанса",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = PlaceRequest.class
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
                                                    implementation = PlaceResponse.class
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
    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<PlaceResponse> create(@RequestBody @Valid PlaceRequest request,
                                                @Parameter(hidden = true) BindingResult bindingResult) {
        DataValidation.checkValidation(bindingResult);
        PlaceResponse response = service.create(request);
        String entityLocation = ResponseUtils.createEntityLocation(response.id());

        return ResponseEntity.created(URI.create(entityLocation)).body(response);
    }

    /**
     * Обновление существующей записи о месте сеанса
     * @param id - идентификатор
     * @param request - объект типа {@link PlaceRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Обновление существующей записи о месте сеанса",
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
                                    implementation = PlaceRequest.class
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
                                            implementation = MovieResponse.class
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
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<PlaceResponse> update(@PathVariable Long id, @RequestBody @Valid PlaceRequest request,
                                                @Parameter(hidden = true) BindingResult bindingResult) {
        DataValidation.checkValidation(bindingResult);
        PlaceResponse response = service.update(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Обновление доступности мест сеанса
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     * @param available - доступность
     */
    @Operation(
            summary = "Обновление доступности мест сеанса",
            parameters = {
                    @Parameter(
                            name = "sessionId",
                            description = "Идентификатор сеанса",
                            in = ParameterIn.QUERY,
                            schema = @Schema(
                                    implementation = Long.class
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "ids",
                            description = "Список идентификаторов мест сеанса",
                            in = ParameterIn.QUERY,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Long.class
                                    )
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "available",
                            description = "Доступность",
                            in = ParameterIn.QUERY,
                            schema = @Schema(
                                    implementation = Boolean.class
                            ),
                            required = true
                    ),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
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
    @PatchMapping("/ids/update/available-places")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<Void> updateAvailabilityAtPlaces(@RequestParam Long sessionId,
                                                           @RequestParam Set<Long> ids,
                                                           @RequestParam Boolean available) {
        service.updateAvailable(sessionId, ids, available);

        return ResponseEntity.ok().build();
    }

    /**
     * Удаление записи о месте сеанса по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Удаление записи о месте сеанса по идентификатору",
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
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<PlaceResponse> deleteById(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
