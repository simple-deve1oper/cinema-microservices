package dev.library.domain.booking.dto;

import dev.library.domain.booking.dto.constant.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO для фильтрации поиска данных по бронированию
 */
@Schema(
        name = "BookingSearchRequest",
        description = "DTO для фильтрации поиска данных по бронированию"
)
public class BookingSearchRequest {
    /**
     * Идентификатор пользователя
     */
    @Schema(name = "userId", description = "Идентификатор пользователя")
    private String userId;
    /**
     * Идентификатор сеанса
     */
    @Schema(name = "sessionId", description = "Идентификатор сеанса")
    private Long sessionId;
    /**
     * Статус бронирования
     */
    @Schema(name = "bookingStatus", description = "Статус бронирования")
    private BookingStatus bookingStatus;
    /**
     * Дата с которой производится поиск
     */
    @Schema(name = "from", description = "Дата и время по которым начинается поиск")
    private LocalDate from;
    /**
     * Дата по которую производится поиск
     */
    @Schema(name = "to", description = "Дата и время по которым закачивается поиск")
    private LocalDate to;

    public BookingSearchRequest() {}

    public BookingSearchRequest(String userId, Long sessionId, BookingStatus bookingStatus, LocalDate from, LocalDate to) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.bookingStatus = bookingStatus;
        this.from = from;
        this.to = to;
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

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingSearchRequest that = (BookingSearchRequest) o;
        return Objects.equals(userId, that.userId) && Objects.equals(sessionId, that.sessionId) && bookingStatus == that.bookingStatus && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, sessionId, bookingStatus, from, to);
    }

    @Override
    public String toString() {
        return "BookingSearchRequest{" +
                "userId='" + userId + '\'' +
                ", sessionId=" + sessionId +
                ", bookingStatus=" + bookingStatus +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
