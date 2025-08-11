package dev.booking.controller;

import dev.booking.entity.Booking;
import dev.booking.service.BookingService;
import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.dto.ApiErrorResponse;
import dev.library.core.util.DataValidation;
import dev.library.core.util.ResponseUtils;
import dev.library.domain.booking.dto.BookingRequest;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.booking.dto.BookingSearchRequest;
import dev.library.domain.booking.dto.BookingStatusRequest;
import dev.library.security.auth.util.RoleUtils;
import dev.library.security.auth.util.UserDataUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Контроллер, в котором представлены методы для работы с бронированием
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Методы для работы с бронированием")
@SecurityRequirement(name = "Keycloak")
public class BookingController {
    private final BookingService service;

    @Value("${errors.booking.user-id.bad-request}")
    private String errorBookingUserIdBadRequest;

    /**
     * Получение всех записей о бронированиях
     * @param searchRequest - объект типа {@link BookingSearchRequest}
     */
    @Operation(
            summary = "Получение всех записей о бронированиях",
            parameters = {
                    @Parameter(
                            name = "searchRequest",
                            description = "Объект для фильтрации поиска бронирований",
                            in = ParameterIn.QUERY,
                            schema = @Schema(
                                    implementation = BookingSearchRequest.class
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
                                                            implementation = BookingResponse.class
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
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<List<BookingResponse>> getAll(@ModelAttribute BookingSearchRequest searchRequest) {
        Authentication authentication = UserDataUtils.getAuthentication();
        if (RoleUtils.checkRole(authentication, "client")) {
            searchRequest.setUserId(authentication.getName());
        }
        List<Booking> bookings = service.getAll(searchRequest);
        List<BookingResponse> response = bookings.stream()
                .map(service::buildResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Получение записи о бронировании по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Получение записи о бронировании по идентификатору",
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
                                            schema = @Schema(
                                                    implementation = BookingResponse.class
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
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id) {
        Authentication authentication = UserDataUtils.getAuthentication();

        Booking booking;
        if (RoleUtils.checkRole(authentication, "client")) {
            booking = service.getById(id, authentication.getName());
        } else {
            booking = service.getById(id);
        }
        BookingResponse bookingResponse = service.buildResponse(booking);

        return ResponseEntity.ok(bookingResponse);
    }

    /**
     * Создание новой записи о бронировании
     * @param request - объект типа {@link BookingRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Создание новой записи о бронировании",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = BookingRequest.class
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
                                                    implementation = BookingResponse.class
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
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<BookingResponse> create(@RequestBody @Valid BookingRequest request,
                                                  @Parameter(hidden = true) BindingResult bindingResult) {
        Authentication authentication = UserDataUtils.getAuthentication();
        if (request.getUserId() == null || RoleUtils.checkRole(authentication, "client")) {
            request.setUserId(authentication.getName());
        }
        DataValidation.checkValidation(bindingResult);
        BookingResponse response = service.create(request);
        String entityLocation = ResponseUtils.createEntityLocation(response.id());

        return ResponseEntity.created(URI.create(entityLocation)).body(response);
    }

    /**
     * Обновление существующей записи о бронировании
     * @param id - идентификатор
     * @param request - объект типа {@link BookingRequest}
     * @param bindingResult - объект типа {@link BindingResult}
     */
    @Operation(
            summary = "Обновление существующей записи о бронировании",
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
                                    implementation = BookingRequest.class
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
                                            implementation = BookingResponse.class
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
    public ResponseEntity<BookingResponse> update(@PathVariable Long id, @RequestBody @Valid BookingRequest request,
                                                  @Parameter(hidden = true) BindingResult bindingResult) {
        if (request.getUserId() == null) {
            throw new BadRequestException(errorBookingUserIdBadRequest);
        }
        DataValidation.checkValidation(bindingResult);
        BookingResponse response = service.update(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Обновление статуса у существующей записи о бронировании
     * @param id - идентификатор
     * @param request - объект типа {@link BookingStatusRequest}
     */
    @Operation(
            summary = "Обновление статуса у существующей записи о бронировании",
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
                                    implementation = BookingStatusRequest.class
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
                                            implementation = BookingResponse.class
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
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<BookingResponse> updateStatus(@PathVariable Long id,
                                                        @RequestBody @Valid BookingStatusRequest request,
                                                        @Parameter(hidden = true) BindingResult bindingResult) {
        Authentication authentication = UserDataUtils.getAuthentication();
        UserDataUtils.checkEmailVerification(authentication);
        if (request.getUserId() == null || RoleUtils.checkRole(authentication, "client")) {
            request.setUserId(authentication.getName());
        }
        DataValidation.checkValidation(bindingResult);
        BookingResponse response = service.updateStatus(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Удаление записи о бронировании по идентификатору
     * @param id - идентификатор
     */
    @Operation(
            summary = "Удаление записи о бронировании по идентификатору",
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
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Проверка на существование бронирования по идентификатору и идентификатору пользователя
     * @param id - идентификатор
     * @param userId - идентификатор пользователя
     */
    @Operation(
            summary = "Проверка на существование бронирования по идентификатору и идентификатору пользователя",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Идентификатор",
                            in = ParameterIn.PATH,
                            schema = @Schema(
                                    implementation = Long.class
                            ),
                            required = true
                    ),
                    @Parameter(
                            name = "userId",
                            description = "Идентификатор пользователя",
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
                                            implementation = Boolean.class
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
                    )
            }
    )
    @GetMapping("/{id}/user")
    public ResponseEntity<Boolean> existsByIdAndUserId(@PathVariable Long id, @RequestParam String userId) {
        boolean result = service.existsByUserId(id, userId);

        return ResponseEntity.ok(result);
    }
}
