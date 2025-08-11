package dev.booking.service.impl;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.booking.mapper.BookingMapper;
import dev.booking.repository.BookingRepository;
import dev.booking.service.BookingPlaceService;
import dev.booking.service.BookingService;
import dev.booking.service.RabbitMQProducer;
import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.core.exception.ServerException;
import dev.library.core.specification.SpecificationBuilder;
import dev.library.core.util.DateUtil;
import dev.library.core.util.ReflectionUtils;
import dev.library.domain.booking.dto.BookingRequest;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.booking.dto.BookingSearchRequest;
import dev.library.domain.booking.dto.BookingStatusRequest;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.movie.client.MovieClient;
import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.rabbitmq.ActionType;
import dev.library.domain.receipt.dto.ReceiptRequest;
import dev.library.domain.session.client.SessionClient;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.client.UserClient;
import dev.library.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link BookingPlace}
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final BookingPlaceService bookingPlaceService;
    private final SessionClient sessionClient;
    private final MovieClient movieClient;
    private final UserClient userClient;
    private final RabbitMQProducer rabbitMQProducer;
    private final SpecificationBuilder<Booking> specificationBuilder;

    @Value("${errors.booking.id.not-found}")
    private String errorBookingIdNotFound;
    @Value("${errors.booking.id-user-id.not-found}")
    private String errorBookingIdAndUserIdNotFound;
    @Value("${errors.booking.id-status.already-exists}")
    private String errorBookingIdAndBookingStatusAlreadyExists;
    @Value("${errors.booking.id-status.bad-request}")
    private String errorBookingIdAndBookingStatusBadRequest;
    @Value("${errors.booking.status.bad-request}")
    private String errorBookingStatusBadRequest;
    @Value("${errors.booking.places.already-exists}")
    private String errorBookingPlacesAlreadyExists;
    @Value("${errors.booking.places.bad-request}")
    private String errorBookingPlacesBadRequest;
    @Value("${errors.booking.session.time-end.bad-request}")
    private String errorBookingSessionTimeEndBadRequest;
    @Value("${errors.booking.session.available.bad-request}")
    private String errorBookingSessionAvailableBadRequest;

    @Override
    public List<Booking> getAll(BookingSearchRequest searchRequest) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Specification<Booking> specification = getSpecificationByBookingSearchRequest(searchRequest);

        return repository.findAll(specification, sort);
    }

    @Override
    public Booking getById(Long id) {
        return findById(id);
    }

    @Override
    public Booking getById(Long id, String userId) {
        checkExistsById(id);

        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(errorBookingIdAndUserIdNotFound.formatted(id, userId)));
    }

    @Override
    public boolean existsByUserId(Long id, String userId) {
        Specification<Booking> specification = getSpecificationByIdAndUserId(id, userId);

        return repository.exists(specification);
    }

    @Override
    @Transactional
    public BookingResponse create(BookingRequest request) {
        if (request.getBookingStatus() == BookingStatus.CANCELED) {
            throw new BadRequestException(errorBookingStatusBadRequest);
        }
        checkMovieStartTime(request.getSessionId());
        checkAvailablePlaces(request.getSessionId(), request.getPlaceIds());
        Booking booking = mapper.toEntity(request);
        booking = repository.save(booking);
        List<BookingPlace> places = bookingPlaceService.create(request.getSessionId(), booking, request.getPlaceIds());
        booking.setPlaces(places);
        BookingResponse response = buildResponse(booking);
        sendMessage(response, ActionType.CREATE);

        return response;
    }

    @Override
    @Transactional
    public BookingResponse update(Long id, BookingRequest request) {
        checkByIdAndBookingStatusCanceledForUpdate(id);
        Booking booking = findById(id);
        Long oldSessionId = booking.getSessionId();
        String oldUserId = booking.getUserId();
        replaceData(booking, request);
        if (booking.getBookingStatus() == BookingStatus.CANCELED) {
            Set<Long> placeIds = booking.getPlaces().stream().map(BookingPlace::getPlaceId).collect(Collectors.toSet());
            bookingPlaceService.updateAvailability(request.getSessionId(), placeIds, Boolean.TRUE);
        } else {
            replacePlaces(oldSessionId, booking, request.getPlaceIds());
        }
        booking = repository.save(booking);
        BookingResponse response = buildResponse(booking);
        if (response.userId().equals(oldUserId)) {
            sendMessage(response, ActionType.UPDATE);
        } else {
            sendMessage(response, ActionType.CREATE);
        }

        return response;
    }

    @Override
    @Transactional
    public BookingResponse updateStatus(Long id, BookingStatusRequest request) {
        checkExistsById(id);
        checkByIdAndUserId(id, request.getUserId());
        checkByIdAndBookingStatusCanceledForUpdate(id);
        BookingStatus status = request.getBookingStatus();
        if (repository.exists(getSpecificationByIdAndBookingStatus(id, request.getBookingStatus()))) {
            String errorMessage = errorBookingIdAndBookingStatusAlreadyExists.formatted(status.getValue(), id);
            throw new EntityAlreadyExistsException(errorMessage);
        }
        Booking booking = findById(id);
        booking.setBookingStatus(status);
        booking = repository.save(booking);
        if (status == BookingStatus.CANCELED) {
            Set<Long> placeIds = booking.getPlaces().stream().map(BookingPlace::getPlaceId).collect(Collectors.toSet());
            bookingPlaceService.updateAvailability(booking.getSessionId(), placeIds, Boolean.TRUE);
        }
        BookingResponse response = buildResponse(booking);
        sendMessage(response, ActionType.UPDATE_STATUS);

        return response;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Booking booking = getById(id);
        repository.delete(booking);
        Set<Long> placeIds = booking.getPlaces().stream().map(BookingPlace::getPlaceId).collect(Collectors.toSet());
        bookingPlaceService.updateAvailability(booking.getSessionId(), placeIds, true);
        if (booking.getBookingStatus() != BookingStatus.CANCELED) {
            sendMessage(id, booking.getUserId());
        }
    }

    /**
     * Отправка сообщения в брокер сообщений о создании или обновлении записи по бронированию
     * @param bookingResponse - объект типа {@link BookingResponse}
     * @param type - перечисление типа {@link ActionType}
     */
    public void sendMessage(BookingResponse bookingResponse, ActionType type) {
        UserResponse userResponse = userClient.getById(bookingResponse.userId());

        ReceiptRequest receiptRequest;
        switch (type) {
            case CREATE:
                receiptRequest = new ReceiptRequest(bookingResponse, userResponse);
                rabbitMQProducer.sendMessage(receiptRequest, ActionType.CREATE);
                break;
            case UPDATE:
                receiptRequest = new ReceiptRequest(bookingResponse, userResponse);
                rabbitMQProducer.sendMessage(receiptRequest, ActionType.UPDATE);
                break;
            case UPDATE_STATUS:
                receiptRequest = new ReceiptRequest(bookingResponse, userResponse);
                rabbitMQProducer.sendMessage(receiptRequest, ActionType.UPDATE_STATUS);
                break;
            default:
                throw new ServerException("Значение для создания и обновления не может быть DELETE");
        }
    }

    @Override
    public void sendMessage(Long id, String userId) {
        UserResponse userResponse = userClient.getById(userId);
        NotificationDeleteRequest notificationDeleteRequest = new NotificationDeleteRequest(id, userResponse);
        rabbitMQProducer.sendMessage(notificationDeleteRequest, ActionType.DELETE);
    }

    @Override
    public BookingResponse buildResponse(Booking booking) {
        List<BookingPlace> places = booking.getPlaces();
        Set<Long> placeIds = places.stream()
                .map(BookingPlace::getPlaceId)
                .collect(Collectors.toSet());
        List<PlaceResponse> placeResponses = bookingPlaceService.getPlaceResponses(placeIds);
        SessionResponse sessionResponse = sessionClient.getById(booking.getSessionId());

        return mapper.toResponse(booking, sessionResponse, placeResponses);
    }

    /**
     * Получение объекта типа {@link Booking} по идентификатору
     * @param id - идентификатор
     */
    private Booking findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorBookingIdNotFound.formatted(id)));
    }

    /**
     * Замена данных записи о бронировании
     * @param booking - объект типа {@link Booking}
     * @param request - объект типа {@link BookingRequest}
     */
    private void replaceData(Booking booking, BookingRequest request) {
        String requestUserId = request.getUserId();
        if (!requestUserId.equals(booking.getUserId())) {
            booking.setUserId(requestUserId);
        }
        Long requestSessionId = request.getSessionId();
        if (!requestSessionId.equals(booking.getSessionId())) {
            checkMovieStartTime(requestSessionId);
            booking.setSessionId(requestSessionId);
        }
        BookingStatus requestStatus = request.getBookingStatus();
        if (!requestStatus.equals(booking.getBookingStatus())) {
            booking.setBookingStatus(requestStatus);
        }

    }

    /**
     * Замена мест для бронирования
     * @param oldSessionId - идентификатор сеанса текущей записи
     * @param booking - объект типа {@link Booking}
     * @param placeIds - список идентификаторов мест
     */
    private void replacePlaces(Long oldSessionId, Booking booking, Set<Long> placeIds) {
        Set<Long> currentPlaceIds = booking.getPlaces().stream().map(BookingPlace::getPlaceId).collect(Collectors.toSet());
        Set<Long> placeIdsForRemove = bookingPlaceService.getIdsForRemove(currentPlaceIds, placeIds);
        Set<Long> placeIdsForCreate = bookingPlaceService.getIdsForCreate(currentPlaceIds, placeIds);
        if (!placeIdsForCreate.isEmpty()) {
            checkAvailablePlaces(booking.getSessionId(), placeIdsForCreate);
        }
        bookingPlaceService.update(oldSessionId, booking, placeIdsForRemove, placeIdsForCreate);
    }

    /**
     * Проверка на существование бронирования по идентификатору
     * @param id - идентификатор
     */
    private void checkExistsById(Long id) {
        if (!repository.existsById(id)) {
            String message = errorBookingIdNotFound.formatted(id);
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Проверка на то, что запись о бронировании существует для определенного пользователя
     * @param id - идентификатор
     * @param userId - идентификатор бронирования
     */
    private void checkByIdAndUserId(Long id, String userId) {
        Specification<Booking> specification = getSpecificationByIdAndUserId(id, userId);
        if (!repository.exists(specification)) {
            String errorMessage = errorBookingIdAndUserIdNotFound.formatted(id, userId);
            throw new EntityNotFoundException(errorMessage);
        }
    }

    /**
     * Проверка на то, что у записи бронировании не стоит статус CANCELED для обновления
     * @param id - идентификатор
     */
    private void checkByIdAndBookingStatusCanceledForUpdate(Long id) {
        Specification<Booking> specification = getSpecificationByIdAndBookingStatus(id, BookingStatus.CANCELED);
        if (repository.exists(specification)) {
            throw new BadRequestException(errorBookingIdAndBookingStatusBadRequest);
        }
    }

    /**
     * Проверка доступности мест
     * @param sessionId - идентификатор сеанса
     * @param placeIds - список идентификаторов мест
     */
    private void checkAvailablePlaces(Long sessionId, Set<Long> placeIds) {
        long placeId = bookingPlaceService.getPlaceNotEqualsSessionBySessionIdAndIds(sessionId, placeIds);
        if (placeId != 0) {
            String errorMessage = errorBookingPlacesBadRequest.formatted(placeId, sessionId);
            throw new EntityNotFoundException(errorMessage);
        }
        placeId = bookingPlaceService.getPlaceBySessionIdAndIdsAndAvailableFalse(sessionId, placeIds);
        if (placeId != 0) {
            String errorMessage = errorBookingPlacesAlreadyExists.formatted(placeId);
            throw new EntityAlreadyExistsException(errorMessage);
        }
    }

    /**
     * Проверка даты и времени начала сеанса фильма
     * @param sessionId - идентификатор сеанса
     */
    private void checkMovieStartTime(Long sessionId) {
        SessionResponse sessionResponse = sessionClient.getById(sessionId);
        if (!sessionResponse.available()) {
            String errorMessage = errorBookingSessionAvailableBadRequest
                    .formatted(sessionId);
            throw new BadRequestException(errorMessage);
        }
        Integer movieDuration = movieClient.getDurationById(sessionResponse.movieId());
        if (OffsetDateTime.now().minusMinutes(movieDuration).isAfter(sessionResponse.dateTime())) {
            String errorMessage = errorBookingSessionTimeEndBadRequest
                    .formatted(DateUtil.formatDate(sessionResponse.dateTime()), sessionResponse.hall());
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * Получение Specification для фильтрации данных при получении всех записей о бронированиях
     * @param searchDto - объект типа {@link BookingSearchRequest}
     */
    private Specification<Booking> getSpecificationByBookingSearchRequest(BookingSearchRequest searchDto) {
        if (searchDto.getUserId() == null || searchDto.getUserId().isBlank()) {
            searchDto.setUserId(null);
        }
        Specification<Booking> specification = specificationBuilder.emptySpecification();
        if (ReflectionUtils.allFieldsIsNull(searchDto)) {
            return specification;
        }
        if (Objects.nonNull(searchDto.getUserId())) {
            String valueUserId = searchDto.getUserId();
            String fieldNameUserId = ReflectionUtils.getFieldName(searchDto, valueUserId).orElseThrow();
            specification = specification.and(
                    specificationBuilder.equal(fieldNameUserId, valueUserId)
            );
        }
        if (Objects.nonNull(searchDto.getSessionId())) {
            Long valueSessionId = searchDto.getSessionId();
            String fieldNameSessionId = ReflectionUtils.getFieldName(searchDto, valueSessionId).orElseThrow();
            specification = specification.and(
                    specificationBuilder.equal(fieldNameSessionId, valueSessionId)
            );
        }
        if (Objects.nonNull(searchDto.getBookingStatus())) {
            BookingStatus valueBookingStatus = searchDto.getBookingStatus();
            String fieldNameBookingStatus = ReflectionUtils.getFieldName(searchDto, valueBookingStatus).orElseThrow();
            specification = specification.and(
                    specificationBuilder.equal(fieldNameBookingStatus, valueBookingStatus)
            );
        }
        if (Objects.nonNull(searchDto.getFrom()) && Objects.nonNull(searchDto.getTo())) {
            OffsetDateTime from = OffsetDateTime.of(searchDto.getFrom(), LocalTime.MIN, ZoneOffset.UTC);
            OffsetDateTime to = OffsetDateTime.of(searchDto.getTo(), LocalTime.MAX, ZoneOffset.UTC);
            specification = specification.and(
                    specificationBuilder.between("createdDate", from, to)
            );
        } else {
            if (Objects.nonNull(searchDto.getFrom())) {
                OffsetDateTime from = OffsetDateTime.of(searchDto.getFrom(), LocalTime.MIN, ZoneOffset.UTC);
                specification = specification.and(
                        specificationBuilder.greaterThanOrEqualToDate("createdDate", from)
                );
            } else if (Objects.nonNull(searchDto.getTo())) {
                OffsetDateTime to = OffsetDateTime.of(searchDto.getTo(), LocalTime.MAX, ZoneOffset.UTC);
                specification = specification.and(
                        specificationBuilder.lessThanOrEqualToDate("createdDate", to)
                );
            }
        }

        return specification;
    }

    /**
     * Получение Specification для фильтрации данных по идентификатору и идентификатору пользователя при получении
     * всех записей о бронированиях
     * @param id - идентификатор
     * @param userId - идентификатор пользователя
     */
    private Specification<Booking> getSpecificationByIdAndUserId(Long id, String userId) {
        return specificationBuilder.equal("id", id)
                .and(specificationBuilder.equal("userId", userId));
    }

    /**
     * Получение Specification для фильтрации данных по идентификатору и статусу при получении всех записей
     * о бронированиях
     * @param id - идентификатор
     * @param bookingStatus - перечисление типа {@link BookingStatus}
     */
    private Specification<Booking> getSpecificationByIdAndBookingStatus(Long id, BookingStatus bookingStatus) {
        return specificationBuilder.equal("id", id)
                .and(specificationBuilder.equal("bookingStatus", bookingStatus));
    }
}
