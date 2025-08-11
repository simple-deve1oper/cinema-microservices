package dev.dictionary.country.controller;

import dev.dictionary.country.service.CountryService;
import dev.library.core.exception.dto.ApiErrorResponse;
import dev.library.domain.dictionary.country.dto.CountryResponse;
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
 * Контроллер, в котором представлены методы для работы со справочником стран
 */
@RestController
@RequestMapping("/api/v1/dictionary/countries")
@RequiredArgsConstructor
@Tag(name = "Методы для работы со справочником стран")
@SecurityRequirement(name = "Keycloak")
public class CountryController {
    private final CountryService service;

    /**
     * Получение всех записей о странах
     */
    @Operation(
            summary = "Получение всех записей о странах",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = CountryResponse.class
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
    public ResponseEntity<List<CountryResponse>> getAll() {
        List<CountryResponse> responses = service.getAll();

        return ResponseEntity.ok(responses);
    }

    /**
     * Поиск записи о стране по коду
     * @param code - код страны
     */
    @Operation(
            summary = "Поиск записи о стране по коду",
            parameters = {
                    @Parameter(
                            name = "code",
                            description = "Код страны",
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
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = CountryResponse.class
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
    @GetMapping("/search")
    public ResponseEntity<CountryResponse> getByCode(@RequestParam String code) {
        CountryResponse response = service.getByCode(code);

        return ResponseEntity.ok(response);
    }

    /**
     * Получение записей стран по переданным кодам
     * @param codes - список кодов
     */
    @Operation(
            summary = "Получение записей стран по переданным кодам",
            parameters = {
                    @Parameter(
                            name = "values",
                            description = "Список кодов",
                            in = ParameterIn.QUERY,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = String.class
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
                                                            implementation = CountryResponse.class
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
    @GetMapping("/search/codes")
    public ResponseEntity<List<CountryResponse>> getAllByCodes(@RequestParam(name = "values") Set<String> codes) {
        List<CountryResponse> responses = service.getAllByCodes(codes);

        return ResponseEntity.ok(responses);
    }

    /**
     * Получение списка кодов стран, которые не принадлежат не одной существующей записи страны
     * @param codes - список кодов
     */
    @Operation(
            summary = "Получение списка кодов стран, которые не принадлежат не одной существующей записи страны",
            parameters = {
                    @Parameter(
                            name = "values",
                            description = "Список кодов",
                            in = ParameterIn.QUERY,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = String.class
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
    @GetMapping("/search/not-exists/codes")
    public ResponseEntity<List<String>> getNonExistentCodes(@RequestParam(name = "values") Set<String> codes) {
        List<String> nonExistentCodes = service.getNonExistentCodes(codes);

        return ResponseEntity.ok(nonExistentCodes);
    }
}
