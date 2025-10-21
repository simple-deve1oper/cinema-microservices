package dev.receipt.service.impl;

import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.booking.client.BookingClient;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.movie.client.MovieClient;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.notification.dto.NotificationRequest;
import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.receipt.dto.ReceiptRequest;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.client.UserClient;
import dev.library.domain.user.dto.UserResponse;
import dev.receipt.entity.Receipt;
import dev.receipt.repository.ReceiptRepository;
import dev.receipt.service.GenerateDocumentService;
import dev.receipt.service.RabbitMQProducer;
import dev.receipt.service.ReceiptService;
import dev.receipt.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link ReceiptService}
 */
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {
    private final ReceiptRepository repository;
    private final TemplateService templateService;
    private final GenerateDocumentService documentService;
    private final MovieClient movieClient;
    private final UserClient userClient;
    private final BookingClient bookingClient;
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${errors.receipt.booking-id.not-found}")
    private String errorReceiptBookingIdNotFound;
    @Value("${errors.receipt.booking-id.already-exists}")
    private String errorReceiptBookingIdAlreadyExists;

    @Override
    public Resource getByBookingId(Long bookingId) {
        Optional<byte[]> optionalData = repository.findDataByBookingId(bookingId);
        byte[] data;
        if (optionalData.isEmpty()) {
            BookingResponse bookingResponse = bookingClient.getById(bookingId);
            data = createReceipt(bookingResponse);
            createEntity(bookingId, bookingResponse.userId(), data);
        } else {
            data = optionalData.get();
        }

        return new ByteArrayResource(data);
    }

    @Override
    @Transactional
    @RabbitListener(queues = {"${rabbitmq.receipt.queue.creation}"})
    public void create(ReceiptRequest request) {
        BookingResponse bookingResponse = request.bookingResponse();
        UserResponse userResponse = request.userResponse();
        Long bookingId = bookingResponse.id();
        boolean existsByBookingIdAndUserId = repository.existsByBookingIdAndUserId(bookingId, userResponse.id());
        if (existsByBookingIdAndUserId) {
            String errorMessage = errorReceiptBookingIdAlreadyExists.formatted(bookingId);
            throw new EntityAlreadyExistsException(errorMessage);
        }
        byte[] data = createReceipt(bookingResponse, userResponse);
        if (repository.existsByBookingId(bookingId)) {
            repository.updateUserIdAndDataByBookingId(bookingId, userResponse.id(), data);
        } else {
            createEntity(bookingId, userResponse.id(), data);
        }
        NotificationRequest notificationRequest = new NotificationRequest(bookingResponse, userResponse, data);
        rabbitMQProducer.sendMessage(notificationRequest, ActionType.CREATE);
    }

    @Override
    @Transactional
    @RabbitListener(queues = {"${rabbitmq.receipt.queue.update}"})
    public void update(ReceiptRequest request) {
        NotificationRequest notificationRequest = updateReceipt(request);
        rabbitMQProducer.sendMessage(notificationRequest, ActionType.UPDATE);
    }

    @Override
    @Transactional
    @RabbitListener(queues = {"${rabbitmq.receipt.queue.update-status}"})
    public void updateStatus(ReceiptRequest request) {
        NotificationRequest notificationRequest = updateReceipt(request);
        rabbitMQProducer.sendMessage(notificationRequest, ActionType.UPDATE_STATUS);
    }

    @Override
    @Transactional
    @RabbitListener(queues = {"${rabbitmq.receipt.queue.delete}"})
    public void deleteByBookingId(NotificationDeleteRequest request) {
        Long bookingId = request.bookingId();
        checkExistsByBookingId(bookingId);
        repository.deleteByBookingId(bookingId);
        rabbitMQProducer.sendMessage(request, ActionType.DELETE);
    }

    private NotificationRequest updateReceipt(ReceiptRequest request) {
        BookingResponse bookingResponse = request.bookingResponse();
        UserResponse userResponse = request.userResponse();
        Long bookingId = bookingResponse.id();
        checkExistsByBookingId(bookingId);
        byte[] data = createReceipt(bookingResponse, userResponse);
        repository.updateDataByBookingId(bookingId, data);

        return new NotificationRequest(bookingResponse, userResponse, data);
    }

    /**
     * Проверка на существование записи о квитанции по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     */
    private void checkExistsByBookingId(Long bookingId) {
        if (!repository.existsByBookingId(bookingId)) {
            throw new EntityNotFoundException(errorReceiptBookingIdNotFound.formatted(bookingId));
        }
    }

    /**
     * Создание квитанции в PDF файле
     * @param bookingResponse - объект типа {@link BookingResponse}
     * @param userResponse - объект типа {@link UserResponse}
     */
    private byte[] createReceipt(BookingResponse bookingResponse, UserResponse userResponse) {
        List<PlaceResponse> placeResponses = bookingResponse.places();
        SessionResponse sessionResponse = bookingResponse.session();
        Set<Long> sessionIds = placeResponses.stream()
                .map(PlaceResponse::sessionId)
                .collect(Collectors.toSet());
        if (sessionIds.size() > 1) {
            throw new BadRequestException("Бронирование не может иметь места с разными сеансами");
        } else {
            Long sessionId = sessionIds.stream().findFirst().orElseThrow();
            if (!sessionId.equals(bookingResponse.session().id())) {
                throw new BadRequestException("Сеанс в бронировании и у мест не может быть разным");
            }
        }
        MovieResponse movieResponse = movieClient.getById(sessionResponse.movieId());
        String content = templateService.createContent(bookingResponse, movieResponse, userResponse);

        return documentService.generateReceipt(content);
    }

    /**
     * Создание квитанции в PDF файле
     * @param bookingResponse - объект типа {@link BookingResponse}
     */
    private byte[] createReceipt(BookingResponse bookingResponse) {
        UserResponse userResponse = userClient.getById(bookingResponse.userId());

        return createReceipt(bookingResponse, userResponse);
    }

    /**
     * Создание новой сущности и её сохранение
     * @param bookingId - идентификатор бронирования
     * @param data - файл в байтовом представлении
     */
    private void createEntity(Long bookingId, String userId, byte[] data) {
        Receipt receipt = Receipt.builder()
                .bookingId(bookingId)
                .userId(userId)
                .data(data)
                .build();
        repository.save(receipt);
    }
}
