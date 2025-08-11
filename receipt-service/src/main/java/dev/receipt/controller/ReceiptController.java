package dev.receipt.controller;

import dev.library.domain.movie.dto.MovieResponse;
import dev.library.security.auth.util.RoleUtils;
import dev.library.security.auth.util.UserDataUtils;
import dev.receipt.service.BookingCheckService;
import dev.receipt.service.ReceiptService;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
@Tag(name = "Методы для работы с квитанциями")
@SecurityRequirement(name = "Keycloak")
public class ReceiptController {
    private final ReceiptService service;
    private final BookingCheckService bookingCheckService;

    /**
     * Получение квитанции по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     */
    @Operation(
            summary = "Получение квитанции по идентификатору бронирования",
            parameters = {
                    @Parameter(
                            name = "booking-id",
                            description = "Идентификатор бронирования",
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
    @GetMapping("/booking/{booking-id}")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'client')")
    public ResponseEntity<Resource> getByBookingId(@PathVariable("booking-id") Long bookingId) {
        Authentication authentication = UserDataUtils.getAuthentication();
        if (RoleUtils.checkRole(authentication, "client")) {
            bookingCheckService.checkExistsByBookingIdAndUserId(bookingId, authentication.getName());
        }
        Resource receipt = service.getByBookingId(bookingId);

        return ResponseEntity.status(HttpStatus.OK.value())
                .header("Content-Type", MediaType.APPLICATION_PDF_VALUE)
                .header("Content-Disposition", "attachment; filename=\"booking_%s.pdf\"".formatted(bookingId))
                .body(receipt);
    }
}
