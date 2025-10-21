package dev.notification.service.impl;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.notification.dto.NotificationRequest;
import dev.library.domain.user.dto.UserResponse;
import dev.notification.service.MailSendingService;
import dev.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Сервис, реализующий интерфейс {@link NotificationService}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final MailSendingService mailSendingService;

    @Value("${template.subject}")
    private String subject;
    @Value("${template.content.create}")
    private String contentCreate;
    @Value("${template.content.update}")
    private String contentUpdate;
    @Value("${template.content.update-status}")
    private String contentUpdateStatus;
    @Value("${template.content.delete}")
    private String contentDelete;

    @Override
    @RabbitListener(queues = {"${rabbitmq.notification.queue.creation}"})
    public void create(NotificationRequest request) {
        log.debug("Started create(NotificationRequest request) with request = {}", request);
        BookingResponse bookingResponse = request.bookingResponse();
        UserResponse userResponse = request.userResponse();
        Resource receipt = new ByteArrayResource(request.data());
        mailSendingService.sendMessage(userResponse.email(), subject.formatted(bookingResponse.id()), contentCreate,
                getFilename(bookingResponse.id()), receipt);
    }

    @RabbitListener(queues = {"${rabbitmq.notification.queue.update}"})
    @Override
    public void update(NotificationRequest request) {
        log.debug("Started update(NotificationRequest request) with request = {}", request);
        BookingResponse bookingResponse = request.bookingResponse();
        UserResponse userResponse = request.userResponse();
        Resource receipt = new ByteArrayResource(request.data());
        mailSendingService.sendMessage(userResponse.email(), subject.formatted(bookingResponse.id()),
                contentUpdate.formatted(bookingResponse.id()), getFilename(bookingResponse.id()), receipt);
    }

    @RabbitListener(queues = {"${rabbitmq.notification.queue.update-status}"})
    @Override
    public void updateStatus(NotificationRequest request) {
        log.debug("Started updateStatus(NotificationRequest request) with request = {}", request);
        BookingResponse bookingResponse = request.bookingResponse();
        UserResponse userResponse = request.userResponse();
        Resource receipt = new ByteArrayResource(request.data());
        mailSendingService.sendMessage(userResponse.email(), subject.formatted(bookingResponse.id()),
                contentUpdateStatus.formatted(bookingResponse.id()), getFilename(bookingResponse.id()), receipt);
    }

    @RabbitListener(queues = {"${rabbitmq.notification.queue.delete}"})
    @Override
    public void delete(NotificationDeleteRequest request) {
        log.debug("Started delete(NotificationDeleteRequest request) with request = {}", request);
        UserResponse userResponse = request.userResponse();
        Long bookingId = request.bookingId();
        mailSendingService.sendMessage(userResponse.email(), subject.formatted(bookingId),
                contentDelete.formatted(bookingId));
    }

    private String getFilename(Long bookingId) {
        return "booking_%s.pdf".formatted(bookingId);
    }
}
