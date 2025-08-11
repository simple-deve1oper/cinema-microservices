package dev.notification.service;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.notification.dto.NotificationRequest;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    final MailSendingService mailSendingService = Mockito.mock(MailSendingService.class);
    final NotificationService service = new NotificationServiceImpl(mailSendingService);

    BookingResponse bookingResponse;
    SessionResponse sessionResponse;
    PlaceResponse placeResponse;
    UserResponse userResponse;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "subject", "Бронь № %d");
        ReflectionTestUtils.setField(service, "contentCreate", "Вы успешно забронировали место на сеанс");
        ReflectionTestUtils.setField(service, "contentUpdate", "Ваша бронь с № %d обновлена");
        ReflectionTestUtils.setField(service, "contentUpdateStatus", "Статус вашей брони с № %d обновлён");
        ReflectionTestUtils.setField(service, "contentDelete", "Ваша бронь с № %d удалена");

        sessionResponse = new SessionResponse(
                2L,
                125L,
                "3D",
                4,
                OffsetDateTime.now().plusDays(2),
                true
        );
        placeResponse = new PlaceResponse(
                1L,
                2L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse = new BookingResponse(
                112L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);
        userResponse = new UserResponse(
                UUID.randomUUID().toString(),
                "max1234",
                "max1234@mail.com",
                true,
                "Макс",
                "Булочкин",
                "1999-01-01",
                new RoleResponse(UUID.randomUUID().toString(), "client"),
                true
        );
    }

    @Test
    void create() {
        Mockito
                .doNothing()
                .when(mailSendingService)
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.any(InputStreamSource.class));

        NotificationRequest request = new NotificationRequest(
                bookingResponse,
                userResponse,
                "<html><p>Content</p></html>".getBytes()
        );
        service.create(request);

        Mockito
                .verify(mailSendingService, Mockito.times(1))
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.any(InputStreamSource.class));
    }

    @Test
    void update() {
        Mockito
                .doNothing()
                .when(mailSendingService)
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.any(InputStreamSource.class));

        NotificationRequest request = new NotificationRequest(
                bookingResponse,
                userResponse,
                "<html><p>Content</p></html>".getBytes()
        );
        service.update(request);

        Mockito
                .verify(mailSendingService, Mockito.times(1))
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.any(InputStreamSource.class));
    }

    @Test
    void updateStatus() {
        Mockito
                .doNothing()
                .when(mailSendingService)
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.any(InputStreamSource.class));

        NotificationRequest request = new NotificationRequest(
                bookingResponse,
                userResponse,
                "<html><p>Content</p></html>".getBytes()
        );
        service.updateStatus(request);

        Mockito
                .verify(mailSendingService, Mockito.times(1))
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.any(InputStreamSource.class));
    }

    @Test
    void delete() {
        Mockito
                .doNothing()
                .when(mailSendingService)
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        NotificationDeleteRequest request = new NotificationDeleteRequest(
                1L,
                userResponse
        );
        service.delete(request);

        Mockito
                .verify(mailSendingService, Mockito.times(1))
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }
}
