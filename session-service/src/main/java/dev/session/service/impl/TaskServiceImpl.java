package dev.session.service.impl;

import dev.library.domain.schedule.dto.TaskResponse;
import dev.session.service.PlaceService;
import dev.session.service.SessionService;
import dev.session.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link TaskService}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final SessionService sessionService;
    private final PlaceService placeService;

    @Override
    @RabbitListener(queues = {"${rabbitmq.session.queue.disable-by-finished}"})
    public void disableByFinishedSession(String sessionId) {
        log.info("Started disableByFinishedSession(Long sessionId) with sessionId = {}", sessionId);
        sessionService.updateAvailable(Long.parseLong(sessionId), false);
        log.debug("Task completed in method disableByFinishedSession(Long sessionId) with sessionId = {}", sessionId);
    }

    @Override
    @RabbitListener(queues = {"${rabbitmq.session.queue.place.update-available}"})
    public void updateAvailablePlacesAfterCheckBookingsBySession(TaskResponse response) {
        log.info("Started updateAvailablePlacesAfterCheckBookingsBySession(TaskResponse response) with response = {}", response);
        String sessionId = (String) response.additionalProperties().get("sessionId");
        Set<Long> placeIds = response.data().keySet().stream()
                        .map(Long::parseLong)
                    .collect(Collectors.toSet());
        Boolean available = (Boolean) response.additionalProperties().get("available");
        placeService.updateAvailable(Long.parseLong(sessionId), placeIds, available);
        log.debug("Task completed in method updateAvailablePlacesAfterCheckBookingsBySession(TaskResponse response) with response = {}", response);
    }
}
