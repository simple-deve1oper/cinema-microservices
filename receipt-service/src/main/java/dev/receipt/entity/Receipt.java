package dev.receipt.entity;

import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Сущность для описания квитанции
 */
@Entity
@Table(name = "receipts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
public class Receipt extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    /**
     * Идентификатор бронирования
     */
    @Column(name = "booking_id", unique = true, nullable = false)
    private Long bookingId;
    /**
     * Идентификатор пользователя
     */
    @Column(name = "user_id", nullable = false)
    private String userId;
    /**
     * Файл квитанции в байтовом представлении
     */
    @Column(name = "data")
    private byte[] data;
}
