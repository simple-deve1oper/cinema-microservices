package dev.receipt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.time.OffsetDateTime;
import java.util.List;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableWireMock({
        @ConfigureWireMock(name = "booking-service", port = 8151),
        @ConfigureWireMock(name = "user-service", port = 8152),
        @ConfigureWireMock(name = "movie-service", port = 8153)
})
public class ReceiptControllerTest extends AbstractControllerTest {
    private static final Logger log = LoggerFactory.getLogger(ReceiptControllerTest.class);

    @InjectWireMock("booking-service")
    WireMockServer mockBookingService;
    @InjectWireMock("user-service")
    WireMockServer mockUserService;
    @InjectWireMock("movie-service")
    WireMockServer mockMovieService;

    RoleResponse roleResponseClient;
    UserResponse userResponseDima;

    SessionResponse sessionResponseFour;

    PlaceResponse placeResponseFortyNineForSessionFour;
    PlaceResponse placeResponseFiftyForSessionFour;

    BookingResponse bookingResponseFour;

    GenreResponse genreResponseComedy;
    GenreResponse genreResponseDetective;
    GenreResponse genreResponseAdventure;
    GenreResponse genreResponseFamily;

    CountryResponse countryResponseGreatBritain;
    CountryResponse countryResponseFrance;
    CountryResponse countryResponseJapan;
    CountryResponse countryResponseUSA;

    ParticipantResponse participantResponseDirectorForMovieTwo;
    ParticipantResponse participantResponseActorOneForMovieTwo;
    ParticipantResponse participantResponseActorTwoForMovieTwo;
    ParticipantResponse participantResponseActorThreeForMovieTwo;
    ParticipantResponse participantResponseActorFourForMovieTwo;
    ParticipantResponse participantResponseActorFiveForMovieTwo;
    ParticipantResponse participantResponseActorSixForMovieTwo;
    ParticipantResponse participantResponseActorSevenForMovieTwo;
    ParticipantResponse participantResponseActorEightForMovieTwo;

    MovieResponse movieResponseTwo;

    @Container
    static RabbitMQContainer RABBIT_MQ_CONTAINER = new RabbitMQContainer(DockerImageName.parse("rabbitmq:4.0.6-management"))
            .withAdminUser("admin")
            .withAdminPassword("admin");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBIT_MQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBIT_MQ_CONTAINER::getAmqpPort);
        registry.add("spring.rabbitmq.username", () -> "admin");
        registry.add("spring.rabbitmq.password", () -> "admin");
    }

    @BeforeAll
    static void setUpAll() {
        int rabbitManagement = RABBIT_MQ_CONTAINER.getMappedPort(15672);
        log.info("rabbitManagement port: {}", rabbitManagement);
    }

    @BeforeEach
    void setUp() {
        roleResponseClient = new RoleResponse(
                "07a441c8-8512-49c9-b5dd-33a420334d4f",
                "client"
        );
        userResponseDima = new UserResponse(
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                "dima1111",
                "dima1111@example.com",
                true,
                "Dima",
                "Lebovski",
                "1990-12-12",
                roleResponseClient,
                true
        );

        sessionResponseFour = new SessionResponse(
                4L,
                2L,
                "3D",
                3,
                OffsetDateTime.now().plusDays(1),
                true
        );

        placeResponseFortyNineForSessionFour = new PlaceResponse(
                49L,
                4L,
                5,
                24,
                "350.00",
                false
        );
        placeResponseFiftyForSessionFour = new PlaceResponse(
                50L,
                4L,
                5,
                25,
                "350.00",
                false
        );

        bookingResponseFour = new BookingResponse(
                4L,
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                sessionResponseFour,
                List.of(placeResponseFortyNineForSessionFour, placeResponseFiftyForSessionFour),
                BookingStatus.CREATED.getValue(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        genreResponseComedy = new GenreResponse(
                1L,
                "Комедии"
        );
        genreResponseDetective = new GenreResponse(
                8L,
                "Детективы"
        );
        genreResponseAdventure = new GenreResponse(
                9L,
                "Приключения"
        );
        genreResponseFamily = new GenreResponse(
                12L,
                "Семейный"
        );

        countryResponseGreatBritain = new CountryResponse(
                5L,
                "826",
                "Великобритания"
        );
        countryResponseFrance = new CountryResponse(
                2L,
                "250",
                "Франция"
        );
        countryResponseJapan = new CountryResponse(
                12L,
                "392",
                "Япония"
        );
        countryResponseUSA = new CountryResponse(
                1L,
                "840",
                "США"
        );

        participantResponseDirectorForMovieTwo = new ParticipantResponse(
                7L,
                "Уилсон",
                "Дагал"
        );
        participantResponseActorOneForMovieTwo = new ParticipantResponse(
                8L,
                "Уишоу",
                "Бен"
        );
        participantResponseActorTwoForMovieTwo = new ParticipantResponse(
                9L,
                "Бонневилль",
                "Хью"
        );
        participantResponseActorThreeForMovieTwo = new ParticipantResponse(
                10L,
                "Коулман",
                "Оливия"
        );
        participantResponseActorFourForMovieTwo = new ParticipantResponse(
                11L,
                "Стонтон",
                "Имельда"
        );
        participantResponseActorFiveForMovieTwo = new ParticipantResponse(
                12L,
                "Уолтерс",
                "Джули"
        );
        participantResponseActorSixForMovieTwo = new ParticipantResponse(
                13L,
                "Бродбент",
                "Джим"
        );
        participantResponseActorSevenForMovieTwo = new ParticipantResponse(
                14L,
                "Бандерас",
                "Антонио"
        );
        participantResponseActorEightForMovieTwo = new ParticipantResponse(
                15L,
                "Зеглер",
                "Рейчел"
        );

        movieResponseTwo = new MovieResponse(
                2L,
                "Приключения Паддингтона 3",
                "Паддингтон, ставший полноправным членом семьи Браунов и гражданином Великобритании, отправляется в гости к тетушке Люси на свою далекую родину — в Перу",
                114,
                2024,
                "6+",
                true,
                List.of(genreResponseComedy, genreResponseDetective, genreResponseAdventure, genreResponseFamily),
                List.of(countryResponseGreatBritain, countryResponseFrance, countryResponseJapan, countryResponseUSA),
                List.of(participantResponseDirectorForMovieTwo),
                List.of(participantResponseActorOneForMovieTwo, participantResponseActorTwoForMovieTwo,
                        participantResponseActorThreeForMovieTwo, participantResponseActorFourForMovieTwo,
                        participantResponseActorFiveForMovieTwo, participantResponseActorSixForMovieTwo,
                        participantResponseActorSevenForMovieTwo, participantResponseActorEightForMovieTwo)
        );
    }

    @Test
    @Order(1)
    void getByBookingId_new() throws JsonProcessingException {
        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+/user"))
                                .withQueryParam("userId", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );
        String bookingResponseFourJson = mapper.writeValueAsString(bookingResponseFour);
        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+"))
                                .willReturn(
                                        WireMock.ok(bookingResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseDimaJson = mapper.writeValueAsString(userResponseDima);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseDimaJson).withHeader("Content-Type", "application/json")
                                )
                );
        String movieResponseTwoJson = mapper.writeValueAsString(movieResponseTwo);
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+"))
                                .willReturn(
                                        WireMock.ok(movieResponseTwoJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 4)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(200)
                .body(Matchers.notNullValue());

        token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 4)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(200)
                .body(Matchers.notNullValue());
    }

    @Test
    @Order(2)
    void getByBookingId_notExistsByBookingIdAndUserId() {
        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+/user"))
                                .withQueryParam("userId", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("false").withHeader("Content-Type", "application/json")
                                )
                );
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 2)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Бронирование с идентификатором 2 для пользователя 3c59a7b2-4cff-49b6-a654-3145ecdab36b не найдено"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(3)
    void getByBookingId_badRequestException_differentSessionsAtPlaces() throws JsonProcessingException {
        PlaceResponse placeResponseOne = new PlaceResponse(
                445L,
                56L,
                2,
                9,
                "200.00",
                false
        );
        PlaceResponse placeResponseTwo = new PlaceResponse(
                446L,
                4L,
                2,
                10,
                "200.00",
                false
        );

        BookingResponse bookingResponse = new BookingResponse(
                332L,
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                sessionResponseFour,
                List.of(placeResponseOne, placeResponseTwo),
                BookingStatus.PAID.getValue(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+/user"))
                                .withQueryParam("userId", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );
        String bookingResponseJson = mapper.writeValueAsString(bookingResponse);
        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+"))
                                .willReturn(
                                        WireMock.ok(bookingResponseJson).withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseDimaJson = mapper.writeValueAsString(userResponseDima);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseDimaJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 446)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Бронирование не может иметь места с разными сеансами"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(3)
    void getByBookingId_badRequestException_differentSessionsAtSessionAndPlaces() throws JsonProcessingException {
        PlaceResponse placeResponseOne = new PlaceResponse(
                445L,
                57L,
                2,
                9,
                "200.00",
                false
        );
        PlaceResponse placeResponseTwo = new PlaceResponse(
                446L,
                57L,
                2,
                10,
                "200.00",
                false
        );

        BookingResponse bookingResponse = new BookingResponse(
                332L,
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                sessionResponseFour,
                List.of(placeResponseOne, placeResponseTwo),
                BookingStatus.PAID.getValue(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+/user"))
                                .withQueryParam("userId", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );
        String bookingResponseJson = mapper.writeValueAsString(bookingResponse);
        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+"))
                                .willReturn(
                                        WireMock.ok(bookingResponseJson).withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseDimaJson = mapper.writeValueAsString(userResponseDima);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseDimaJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 446)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Сеанс в бронировании и у мест не может быть разным"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(4)
    void getByBookingId_bookingService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 7)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис бронирования временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(5)
    void getByBookingId_userService_unavailable() throws JsonProcessingException {
        PlaceResponse placeResponse = new PlaceResponse(
                500L,
                4L,
                2,
                10,
                "200.00",
                false
        );

        BookingResponse bookingResponse = new BookingResponse(
                332L,
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                sessionResponseFour,
                List.of(placeResponse),
                BookingStatus.PAID.getValue(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+/user"))
                                .withQueryParam("userId", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );
        String bookingResponseJson = mapper.writeValueAsString(bookingResponse);
        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+"))
                                .willReturn(
                                        WireMock.ok(bookingResponseJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 7)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис пользователей временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(6)
    void getByBookingId_movieService_unavailable() throws JsonProcessingException {
        PlaceResponse placeResponse = new PlaceResponse(
                500L,
                4L,
                2,
                10,
                "200.00",
                false
        );

        BookingResponse bookingResponse = new BookingResponse(
                332L,
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                sessionResponseFour,
                List.of(placeResponse),
                BookingStatus.PAID.getValue(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+/user"))
                                .withQueryParam("userId", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );
        String bookingResponseJson = mapper.writeValueAsString(bookingResponse);
        mockBookingService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/bookings/[0-9]+"))
                                .willReturn(
                                        WireMock.ok(bookingResponseJson).withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseDimaJson = mapper.writeValueAsString(userResponseDima);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseDimaJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("booking-id", 7)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис фильмов временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(7)
    void getByBookingId_unauthorized() {
        RestAssured
                .given()
                .pathParam("booking-id", 7)
                .when()
                .get("/api/v1/receipts/booking/{booking-id}")
                .then()
                .log().all()
                .statusCode(401);
    }
}
