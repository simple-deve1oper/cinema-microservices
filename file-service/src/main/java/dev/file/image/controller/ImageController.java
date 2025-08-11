package dev.file.image.controller;

import dev.file.image.service.ImageService;
import dev.library.core.exception.dto.ApiErrorResponse;
import dev.library.core.util.DataValidation;
import dev.library.domain.file.dto.ImageRequest;
import dev.library.domain.file.dto.ImageResponse;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер, в котором представлены методы для работы с изображениями для фильмов
 */
@RestController
@RequestMapping("/api/v1/file/images")
@RequiredArgsConstructor
@Tag(name = "Методы для работы с изображениями для фильмов")
@SecurityRequirement(name = "Keycloak")
public class ImageController {
    private final ImageService imageService;

    /**
     * Получение всех записей об изображениях
     */
    @Operation(
            summary = "Получение записей всех изображений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(
                                                            implementation = ImageResponse.class
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
    public ResponseEntity<List<ImageResponse>> getAll() {
        List<ImageResponse> images = imageService.getAll();

        return ResponseEntity.ok(images);
    }

    /**
     * Получение записей изображений по идентификатору фильму
     * @param movieId - идентификатор фильма
     */
    @Operation(
            summary = "Получение записей всех изображений по идентификатору фильму",
            parameters = {
                    @Parameter(
                            name = "movie-id",
                            description = "Идентификатор фильма",
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
                                                            implementation = ImageResponse.class
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
    @GetMapping("/movie/{movie-id}")
    public ResponseEntity<List<ImageResponse>> getAllByMovieId(@PathVariable("movie-id") Long movieId) {
        List<ImageResponse> images = imageService.getAllByMovieId(movieId);

        return ResponseEntity.ok(images);
    }

    /**
     * Получение объекта изображения по идентификатору фильма и порядковому номеру изображения
     * @param movieId - идентификатор фильма
     * @param number - порядковый номер изображения
     */
    @Operation(
            summary = "Получение объекта изображения по идентификатору фильма и порядковому номеру изображения",
            parameters = {
                    @Parameter(
                            name = "movie-id",
                            description = "Идентификатор фильма",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = Long.class
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "number",
                            description = "Порядковый номер изображения",
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
                                    mediaType = "application/octet-stream",
                                    schema = @Schema(
                                            implementation = Resource.class
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
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            implementation = ApiErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @GetMapping(path = "/resource/{movie-id}/{number}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getResourceByMovieIdAndNumber(@PathVariable("movie-id") Long movieId,
                                                                  @PathVariable Integer number) {
        Resource resource = imageService.getResourceByMovieIdAndNumber(movieId, number);

        return ResponseEntity.ok(resource);
    }

    /**
     * Создание нового изображения для определенного фильма
     */
    @Operation(
            summary = "Создание нового изображения для определенного фильма",
            parameters = {
                    @Parameter(
                            name = "movie-id",
                            description = "Идентификатор фильма",
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
                                                            implementation = ImageResponse.class
                                                    )
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
    @PostMapping(value = "/movie/{movie-id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<Void> create(@PathVariable("movie-id") Long movieId, @Parameter(schema = @Schema(type = "string", format = "binary")) @RequestPart("image") MultipartFile image) {
        imageService.create(movieId, image);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Изменение порядкового номера в записях об изображениях
     * @param requests - список объектов типа {@link ImageRequest}
     */
    @Operation(
            summary = "Изменение порядкового номера в записях об изображениях",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = ImageRequest.class
                                    )
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    schema = @Schema(
                                            hidden = true
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
                    )
            }
    )
    @PutMapping("/numbers")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<Void> updateImageNumbers(@RequestBody @Valid List<ImageRequest> requests, @Parameter(hidden = true) BindingResult bindingResult) {
        DataValidation.checkValidation(bindingResult);
        imageService.updateImageNumbers(requests);

        return ResponseEntity.ok().build();
    }

    /**
     * Удаление изображения по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Удаление записи по идентификатору",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = UUID.class
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
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        imageService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
