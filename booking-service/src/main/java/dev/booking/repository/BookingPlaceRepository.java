package dev.booking.repository;

import dev.booking.entity.BookingPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для сущности {@link BookingPlace}
 */
@Repository
public interface BookingPlaceRepository extends JpaRepository<BookingPlace, Long> {}
