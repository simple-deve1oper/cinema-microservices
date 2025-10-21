package dev.booking.service.impl;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.booking.mapper.BookingPlaceMapper;
import dev.booking.repository.BookingPlaceRepository;
import dev.booking.service.BookingPlaceService;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.session.client.PlaceClient;
import dev.library.domain.session.dto.PlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link BookingPlaceService}
 */
@Service
@RequiredArgsConstructor
public class BookingPlaceServiceImpl implements BookingPlaceService {
    private final BookingPlaceRepository repository;
    private final BookingPlaceMapper mapper;
    private final PlaceClient placeClient;

    @Override
    public List<PlaceResponse> getPlaceResponses(Set<Long> placeIds) {
        return placeClient.getAllByIds(placeIds);
    }

    @Override
    public long getPlaceBySessionIdAndIdsAndAvailableFalse(Long sessionId, Set<Long> ids) {
        return placeClient.getPlaceBySessionIdAndIdsAndAvailable(sessionId, ids, false);
    }

    @Override
    public long getPlaceNotEqualsSessionBySessionIdAndIds(Long sessionId, Set<Long> ids) {
        return placeClient.getPlaceNotEqualsSessionBySessionIdAndIds(sessionId, ids);
    }

    @Override
    @Transactional
    public List<BookingPlace> create(Long sessionId, Booking booking, Set<Long> placeIds) {
        List<BookingPlace> bookingPlaces = placeIds.stream()
                .map(placeId -> mapper.toEntity(booking, placeId))
                .toList();
        updateAvailability(sessionId, placeIds, Boolean.FALSE);

        return repository.saveAll(bookingPlaces);
    }

    @Override
    @Transactional
    public void update(Long oldSessionId, Booking booking, Set<Long> placeIdsForRemove,
                                     Set<Long> placeIdsForCreate) {
        if (!placeIdsForRemove.isEmpty()) {
            booking.getPlaces().removeIf(place -> placeIdsForRemove.contains(place.getPlaceId()));
            updateAvailability(oldSessionId, placeIdsForRemove, Boolean.TRUE);
        }
        if (!placeIdsForCreate.isEmpty()) {
            Long newSessionId = booking.getSessionId();
            List<BookingPlace> createdPlaces = create(newSessionId, booking, placeIdsForCreate);
            booking.getPlaces().addAll(createdPlaces);
            updateAvailability(newSessionId, placeIdsForCreate, Boolean.FALSE);
        }
    }

    @Override
    public void updateAvailability(Long sessionId, Set<Long> placeIds, Boolean value) {
        placeClient.updateAvailabilityAtPlaces(sessionId, placeIds, value);
    }

    @Override
    public Set<Long> getIdsForRemove(Set<Long> currentPlaceIds, Set<Long> placeIds) {
        return currentPlaceIds.stream()
                .filter(placeId -> !placeIds.contains(placeId))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getIdsForCreate(Set<Long> currentPlaceIds, Set<Long> placeIds) {
        return placeIds.stream()
                .filter(placeId -> !currentPlaceIds.contains(placeId))
                .collect(Collectors.toSet());
    }

    @Override
    public List<BookingPlace> findByBooking_SessionIdAndBooking_BookingStatus(Long sessionId, BookingStatus bookingStatus) {
        return repository.findByBooking_SessionIdAndBooking_BookingStatus(sessionId, bookingStatus);
    }
}
