package dev.session.entity;

import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Сущность для описания места сеанса
 */
@Entity
@Table(name = "places")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"session"})
@Builder
public class Place extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Сеанс
     */
    @ManyToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private Session session;
    /**
     * Ряд
     */
    @Column(name = "row", nullable = false)
    private Integer row;
    /**
     * Номер
     */
    @Column(name = "number", nullable = false)
    private Integer number;
    /**
     * Цена
     */
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    /**
     * Доступность
     */
    @Column(name = "available", nullable = false)
    private Boolean available;
}
