package dev.receipt.service;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.receipt.service.impl.TemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceImplTest {
    final TemplateService service = new TemplateServiceImpl();

    SessionResponse sessionResponse;
    PlaceResponse placeResponse;
    BookingResponse bookingResponse;
    GenreResponse genreResponse;
    CountryResponse countryResponse;
    ParticipantResponse participantResponseDirector;
    ParticipantResponse participantResponseActor;
    MovieResponse movieResponse;
    UserResponse userResponse;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "currencySign", "₽");

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
                1L,
                1,
                1,
                "200.00",
                true
        );
        bookingResponse = new BookingResponse(
                14L,
                UUID.randomUUID().toString(),
                sessionResponse,
                List.of(placeResponse),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
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
    void createContent() {
        service.createContent(bookingResponse, movieResponse, userResponse);
    }
}
