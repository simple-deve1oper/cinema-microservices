package dev.library.domain.booking.dto;

import dev.library.domain.booking.dto.constant.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;
import java.util.Set;

/**
 * DTO для создания/обновления данных по бронированию
 */
@Schema(
        name = "BookingRequest",
        description = "DTO для создания/обновления данных по бронированию"
)
public class BookingRequest {
    /**
     * Идентификатор пользователя
     */
    @Schema(name = "userId", description = "Идентификатор пользователя", nullable = true)
    @Length(max = 36, message = "Идентификатор пользователя не может содержать более 36 символов")
    private String userId;
    /**
     * Идентификатор сеанса
     */
    @Schema(name = "sessionId", description = "Идентификатор сеанса")
    @NotNull(message = "Идентификатор сеанса не может быть пустым")
    @Min(value = 1, message = "Минимальное значение идентификатора сеанса 1")
    private Long sessionId;
    /**
     * Список идентификаторов мест
     */
    @Schema(name = "placeIds", description = "Список идентификаторов мест")
    //@NotNull(message = "Список идентификаторов мест не могут быть пустыми")
    @NotEmpty(message = "Список идентификаторов мест должны содержать хотя бы один элемент")
    private Set<Long> placeIds;
    /**
     * Статус бронирования
     */
    @Schema(name = "bookingStatus", description = "Статус бронирования")
    @NotNull(message = "Статус бронирования не может быть пустым")
    private BookingStatus bookingStatus;

    public BookingRequest() {}

    public BookingRequest(Long sessionId, Set<Long> placeIds, BookingStatus bookingStatus) {
        this.sessionId = sessionId;
        this.placeIds = placeIds;
        this.bookingStatus = bookingStatus;
    }

    public BookingRequest(String userId, Long sessionId, Set<Long> placeIds, BookingStatus bookingStatus) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.placeIds = placeIds;
        this.bookingStatus = bookingStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Set<Long> getPlaceIds() {
        return placeIds;
    }

    public void setPlaceIds(Set<Long> placeIds) {
        this.placeIds = placeIds;
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
        BookingRequest that = (BookingRequest) o;
        return Objects.equals(userId, that.userId) && Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(placeIds, that.placeIds) && bookingStatus == that.bookingStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, sessionId, placeIds, bookingStatus);
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "userId='" + userId + '\'' +
                ", sessionId=" + sessionId +
                ", placeIds=" + placeIds +
                ", bookingStatus=" + bookingStatus +
                '}';
    }
}
