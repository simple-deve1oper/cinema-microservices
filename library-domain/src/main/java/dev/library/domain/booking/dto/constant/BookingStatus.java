package dev.library.domain.booking.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания статусов бронирования
 */
@Schema(
        name = "BookingStatus",
        description = "Перечисление для описания статусов бронирования"
)
public enum BookingStatus {
    CREATED("Created"), PAID("Paid"), CANCELED("Canceled");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
