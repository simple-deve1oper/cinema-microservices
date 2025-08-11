package dev.session.service.impl;

import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.session.dto.PlaceRequest;
import dev.library.domain.session.dto.PlaceResponse;
import dev.session.entity.Place;
import dev.session.entity.Session;
import dev.session.mapper.PlaceMapper;
import dev.session.repository.PlaceRepository;
import dev.session.service.PlaceService;
import dev.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис, реализующий интерфейс {@link PlaceService}
 */
@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository repository;
    private final PlaceMapper mapper;
    private final SessionService sessionService;

    @Value("${errors.place.id.not-found}")
    private String errorPlaceIdNotFound;
    @Value("${errors.place.session-id-row-number.already-exists}")
    private String errorPlaceSessionIdAndRowAndNumberAlreadyExists;

    @Override
    public List<PlaceResponse> getAll() {
        List<Place> places = repository.findAll();

        return places.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<PlaceResponse> getAllBySession_Id(Long sessionId) {
        List<Place> places = repository.findAllBySession_Id(sessionId);

        return places.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<PlaceResponse> getAllByIds(Set<Long> ids) {
        List<Place> places = repository.findAllByIds(ids);

        return places.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PlaceResponse getById(Long id) {
        Place place = findById(id);

        return mapper.toResponse(place);
    }

    @Override
    @Transactional
    public PlaceResponse create(PlaceRequest request) {
        checkSessionIdAndRowAndNumber(request.sessionId(), request.row(), request.number());
        Session session = sessionService.findById(request.sessionId());
        Place place = mapper.toEntity(request);
        place.setSession(session);
        place = repository.save(place);

        return mapper.toResponse(place);
    }

    @Override
    @Transactional
    public PlaceResponse update(Long id, PlaceRequest request) {
        Place place = findById(id);
        replaceData(place, request);
        place = repository.save(place);

        return mapper.toResponse(place);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            String errorMessage = errorPlaceIdNotFound.formatted(id);
            throw new EntityNotFoundException(errorMessage);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateAvailable(Long sessionId, Set<Long> ids, Boolean available) {
        repository.updateAvailable(sessionId, ids, available);
    }

    @Override
    public Long getPlaceNotEqualsSessionBySessionIdAndIds(Long sessionId, Set<Long> ids) {
        Optional<Long> optionalId = repository.findPlaceNotEqualsSessionBySessionIdAndIds(sessionId, ids);

        return optionalId.isPresent() ? optionalId.get() : 0;
    }

    @Override
    public Long getPlaceBySessionIdAndIdsAndAvailable(Long sessionId, Set<Long> ids, Boolean available) {
        Optional<Long> optionalId = repository.findPlaceBySessionIdAndAvailableAndIds(sessionId, available, ids);

        return optionalId.isPresent() ? optionalId.get() : 0;
    }

    /**
     * Получение сущности {@link Place} по идентификатору
     * @param id - идентификатор
     */
    private Place findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorPlaceIdNotFound.formatted(id)));
    }

    /**
     * Обновление данных записи о месте
     * @param place - объект типа {@link Place}
     * @param request - объект типа {@link PlaceRequest}
     */
    private void replaceData(Place place, PlaceRequest request) {
        Long requestSessionId = request.sessionId();
        if (!requestSessionId.equals(place.getSession().getId())) {
            Session session = sessionService.findById(requestSessionId);
            place.setSession(session);
        }
        Integer requestRow = request.row();
        Integer requestNumber = request.number();
        if (!requestRow.equals(place.getRow()) || !requestNumber.equals(place.getNumber())) {
            checkSessionIdAndRowAndNumber(requestSessionId, requestRow, requestNumber);
            if (!requestRow.equals(place.getRow())) {
                place.setRow(requestRow);
            }
            if (!requestNumber.equals(place.getNumber())) {
                place.setNumber(requestNumber);
            }
        }
        BigDecimal requestPrice = request.price();
        if (!requestPrice.equals(place.getPrice())) {
            place.setPrice(requestPrice);
        }
        Boolean requestAvailable = request.available();
        if (!requestAvailable.equals(place.getAvailable())) {
            place.setAvailable(requestAvailable);
        }
    }

    /**
     * Проверка существование места по идентификатору сеанса, ряду и номеру
     * @param sessionId - идентификатор сеанса
     * @param row - ряд
     * @param number - номер
     */
    private void checkSessionIdAndRowAndNumber(Long sessionId, Integer row, Integer number) {
        boolean result = repository.existsBySession_IdAndRowAndNumber(sessionId, row, number);
        if (result) {
            throw new EntityAlreadyExistsException(errorPlaceSessionIdAndRowAndNumberAlreadyExists
                    .formatted(row, number));
        }
    }
}
