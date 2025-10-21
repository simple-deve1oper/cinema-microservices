package dev.receipt.service;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.notification.dto.NotificationRequest;
import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.receipt.service.impl.RabbitMQProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RabbitMQProducerImplTest {
    final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    final RabbitMQProducer service = new RabbitMQProducerImpl(rabbitTemplate);

    BookingResponse bookingResponse;
    SessionResponse sessionResponse;
    PlaceResponse placeResponse;
    UserResponse userResponse;
    GenreResponse genreResponse;
    CountryResponse countryResponse;
    ParticipantResponse participantResponseDirector;
    ParticipantResponse participantResponseActor;
    MovieResponse movieResponse;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "notificationExchange", "exchange_notification");
        ReflectionTestUtils.setField(service, "creationNotificationRoutingKey", "creation_notification_routing_key");
        ReflectionTestUtils.setField(service, "updateNotificationRoutingKey", "update_notification_routing_key");
        ReflectionTestUtils.setField(service, "deleteNotificationRoutingKey", "delete_notification_routing_key");

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
        genreResponse = new GenreResponse(
                1L,
                "Боевик"
        );
        countryResponse = new CountryResponse(
                1L,
                "999",
                "Тест"
        );
        participantResponseDirector = new ParticipantResponse(
                1L,
                "Петров",
                "Андрей",
                "Иванович"
        );
        participantResponseDirector = new ParticipantResponse(
                2L,
                "Крутой",
                "Майкл"
        );
        movieResponse = new MovieResponse(
                56L,
                "Тест",
                "Тест",
                111,
                2022,
                "18+",
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        movieResponse.genres().add(genreResponse);
        movieResponse.countries().add(countryResponse);
        movieResponse.directors().add(participantResponseDirector);
        movieResponse.actors().add(participantResponseActor);
    }

    @Test
    void sendMessage() {
        Mockito
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));

        NotificationRequest notificationRequest = new NotificationRequest(
                bookingResponse,
                userResponse,
                "<html><p>Content</p></html>".getBytes()
        );
        NotificationDeleteRequest notificationDeleteRequest = new NotificationDeleteRequest(
                112L,
                userResponse
        );
        service.sendMessage(notificationRequest, ActionType.CREATE);
        service.sendMessage(notificationRequest, ActionType.UPDATE);
        service.sendMessage(notificationRequest, ActionType.UPDATE_STATUS);
        service.sendMessage(notificationDeleteRequest, ActionType.DELETE);

        Mockito
                .verify(rabbitTemplate, Mockito.times(3))
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));
    }
}
