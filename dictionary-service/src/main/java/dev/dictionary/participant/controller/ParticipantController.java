package dev.dictionary.participant.controller;

import dev.dictionary.participant.service.ParticipantService;
import dev.library.core.exception.dto.ApiErrorResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Контроллер, в котором представлены методы для работы со справочником участников фильмов
 */
@RestController
@RequestMapping("/api/v1/dictionary/participants")
@RequiredArgsConstructor
@Tag(name = "Методы для работы со справочником участников фильмов")
@SecurityRequirement(name = "Keycloak")
public class ParticipantController {
    private final ParticipantService service;

    /**
     * Получение всех записей об участниках фильмов
     */
    @Operation(
            summary = "Получение всех записей об участниках фильмов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = ParticipantResponse.class
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
    @GetMapping
    public ResponseEntity<List<ParticipantResponse>> getAll() {
        List<ParticipantResponse> responses = service.getAll();

        return ResponseEntity.ok(responses);
    }

    /**
     * Поиск записи об участнике фильма по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Поиск записи об участнике фильма по идентификатору",
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
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ParticipantResponse.class
                                    )
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
    public ResponseEntity<ParticipantResponse> getById(@PathVariable Long id) {
        ParticipantResponse response = service.getById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Получение записей участников фильмов по переданным идентификаторам
     * @param values - список идентификаторов
     */
    @Operation(
            summary = "Получение записей участников фильмов по переданным идентификаторам",
            parameters = {
                    @Parameter(
                            name = "values",
                            description = "Список идентификаторов",
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
                                                            implementation = ParticipantResponse.class
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
    public ResponseEntity<List<ParticipantResponse>> getAllByIds(@RequestParam Set<Long> values) {
        List<ParticipantResponse> responses = service.getAllByIds(values);

        return ResponseEntity.ok(responses);
    }

    /**
     * Получение списка идентификаторов участников фильма, которые не принадлежат не одной существующей записи участников фильмов
     * @param ids - список идентификаторов
     */
    @Operation(
            summary = "Получение списка идентификаторов участников фильма, которые не принадлежат не одной существующей записи участников фильмов",
            parameters = {
                    @Parameter(
                            name = "values",
                            description = "Список идентификаторов",
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
                                                            implementation = String.class
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
    @GetMapping("/search/not-exists/ids")
    public ResponseEntity<List<Long>> getNonExistentIds(@RequestParam(name = "values") Set<Long> ids) {
        List<Long> nonExistentIds = service.getNonExistentIds(ids);

        return ResponseEntity.ok(nonExistentIds);
    }
}
