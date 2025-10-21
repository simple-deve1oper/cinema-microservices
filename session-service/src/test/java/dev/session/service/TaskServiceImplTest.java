package dev.session.service;

import dev.library.domain.schedule.dto.TaskResponse;
import dev.session.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    final SessionService sessionService = Mockito.mock(SessionService.class);
    final PlaceService placeService = Mockito.mock(PlaceService.class);
    final TaskService service = new TaskServiceImpl(sessionService, placeService);

    @Test
    void disableByFinishedSession() {
        Mockito
                .doNothing()
                .when(sessionService)
                .updateAvailable(Mockito.anyLong(), Mockito.anyBoolean());

        service.disableByFinishedSession("1");
        service.disableByFinishedSession("2");

        Mockito
                .verify(sessionService, Mockito.times(2))
                .updateAvailable(Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    void updateAvailablePlacesAfterCheckBookingsBySession() {
        Mockito
                .doNothing()
                .when(placeService)
                .updateAvailable(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());

        TaskResponse taskResponse = new TaskResponse(
                Map.of(
                        "1", 1L,
                        "2", 2L
                ),
                Map.of(
                        "sessionId", "123",
                        "available", true
                )
        );
        service.updateAvailablePlacesAfterCheckBookingsBySession(taskResponse);

        Mockito
                .verify(placeService, Mockito.times(1))
                .updateAvailable(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }
}
