package dev.library.domain.booking.dto;

import dev.library.domain.booking.dto.constant.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

/**
 * DTO для изменения статуса бронирования
 */
@Schema(
        name = "BookingStatusRequest",
        description = "DTO для изменения статуса бронирования"
)
public class BookingStatusRequest {
    /**
     * Идентификатор пользователя
     */
    @Schema(name = "userId", description = "Идентификатор пользователя")
    @Length(max = 36, message = "Идентификатор пользователя не может содержать более 36 символов")
    private String userId;
    /**
     * Статус бронирования
     */
    @Schema(name = "bookingStatus", description = "Статус бронирования")
    @NotNull(message = "Статус бронирования не может быть пустым")
    private BookingStatus bookingStatus;

    private BookingStatusRequest() {}

    public BookingStatusRequest(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public BookingStatusRequest(String userId, BookingStatus bookingStatus) {
        this(bookingStatus);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingStatusRequest that = (BookingStatusRequest) o;
        return Objects.equals(userId, that.userId) && bookingStatus == that.bookingStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, bookingStatus);
    }

    @Override
    public String toString() {
        return "BookingStatusRequest{" +
                "userId='" + userId + '\'' +
                ", bookingStatus=" + bookingStatus +
                '}';
    }
}
