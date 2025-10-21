package dev.user.service;

import dev.library.domain.schedule.dto.TaskResponse;

public interface TaskService {
    void deactivateIfNotEmailVerified(String userId);
    void deleteRecordsIfNotActive(TaskResponse response);
}
