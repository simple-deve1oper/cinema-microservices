package dev.booking.entity;

import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность для описания места бронирования
 */
@Entity
@Table(name = "booking_places")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
public class BookingPlace extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Идентификатор бронирования
     */
    @ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id", nullable = false)
    private Booking booking;
    /**
     * Идентификатор места
     */
    @Column(name = "place_id", nullable = false)
    private Long placeId;
}
