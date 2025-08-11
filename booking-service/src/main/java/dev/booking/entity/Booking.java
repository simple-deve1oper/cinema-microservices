package dev.booking.entity;

import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.List;

/**
 * Сущность для описания бронирования
 */
@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"places"})
@Builder
public class Booking extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Идентификатор пользователя
     */
    @Column(name = "user_id", nullable = false)
    private String userId;
    /**
     * Идентификатор сеанса
     */
    @Column(name = "session_id", nullable = false)
    private Long sessionId;
    /**
     * Статус бронирования
     */
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    private BookingStatus bookingStatus;

    /**
     * Список мест
     */
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingPlace> places;
}
