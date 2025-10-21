package dev.booking.service.impl;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.booking.service.BookingPlaceService;
import dev.booking.service.BookingService;
import dev.booking.service.RabbitMQProducer;
import dev.booking.service.TaskService;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.schedule.dto.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link TaskService}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final BookingService bookingService;
    private final BookingPlaceService bookingPlaceService;
    private final RabbitMQProducer rabbitMQProducer;

    @Override
    @RabbitListener(queues = {"${rabbitmq.booking.queue.check-by-session}"})
    public void checkBookingsBySessionId(String sessionId) {
        log.debug("Started checkBookings(String sessionId) with sessionId = {}", sessionId);
        if (bookingService.existsBySessionId(Long.parseLong(sessionId))) {
            List<BookingPlace> places = bookingPlaceService
                    .findByBooking_SessionIdAndBooking_BookingStatus(Long.parseLong(sessionId), BookingStatus.CREATED);
            log.debug("{} places received for sessionId = {} and status = {}", places.size(), sessionId,
                    BookingStatus.CREATED);
            if (!places.isEmpty()) {
                Set<Long> bookingIds = places.stream()
                        .map(BookingPlace::getBooking)
                        .map(Booking::getId)
                        .collect(Collectors.toSet());
                bookingService.updateStatus(bookingIds, BookingStatus.CANCELED);
                Map<String, Object> data = new HashMap<>();
                places.forEach(place ->
                        data.put(place.getPlaceId().toString(), place.getId())
                );
                TaskResponse response = new TaskResponse(
                        data, Map.of("sessionId", sessionId, "available", true)
                );
                rabbitMQProducer.sendMessage(response);
            }
        } else {
            log.debug("Not exists bookings with sessionId = {}", sessionId);
        }
        log.debug("Task completed in method checkBookings(Long sessionId) with sessionId = {}", sessionId);
    }
}
