package dev.user.service.impl;

import dev.library.core.exception.ServerException;
import dev.library.domain.schedule.dto.TaskResponse;
import dev.library.domain.schedule.constant.UserState;
import dev.library.domain.user.dto.UserResponse;
import dev.user.service.KeycloakUserService;
import dev.user.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final KeycloakUserService service;

    @Override
    @RabbitListener(queues = {"${rabbitmq.user.queue.email-verified}"})
    public void deactivateIfNotEmailVerified(String userId) {
        log.debug("Started deleteIfNotEmailVerified(String userId) with id = {}", userId);
        UserResponse response = service.getById(userId);
        if (!response.emailVerified()) {
            log.debug("A user with id {} hasn't verified email become his an account will be inactivated", userId);
            service.updateActivity(userId);
        }
        log.debug("Task completed in method deleteIfNotEmailVerified(String userId) with id = {}", userId);
    }

    @Override
    @RabbitListener(queues = {"${rabbitmq.user.queue.delete-inactive}"})
    public void deleteRecordsIfNotActive(TaskResponse response) {
        log.debug("Started deleteRecordsIfNotActive(TaskResponse response) with response = {}", response);
        if (!response.data().containsKey("userState")) {
            throw new ServerException("Value with key userState is not set");
        }
        String userState = (String) response.data().get("userState");
        UserState state = UserState.valueOf(userState);
        if (state == UserState.DELETE_INACTIVE) {
            List<UserResponse> userResponses = service.getAll()
                    .stream()
                    .filter(user -> !user.active())
                    .toList();
            for (UserResponse userResponse : userResponses) {
                service.deleteById(userResponse.id());
            }
            log.debug("Deleted {} users", userResponses.size());
        }
        log.debug("Task completed in method deleteRecordsIfNotActive(TaskResponse response) with response = {}", response);
    }
}
