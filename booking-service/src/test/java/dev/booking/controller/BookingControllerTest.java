package dev.booking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.library.core.util.DateUtil;
import dev.library.domain.booking.dto.BookingRequest;
import dev.library.domain.booking.dto.BookingSearchRequest;
import dev.library.domain.booking.dto.BookingStatusRequest;
import dev.library.domain.booking.dto.constant.BookingStatus;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableWireMock({
        @ConfigureWireMock(name = "movie-service", port = 8141),
        @ConfigureWireMock(name = "place-service", port = 8142),
        @ConfigureWireMock(name = "session-service", port = 8143),
        @ConfigureWireMock(name = "user-service", port = 8144),
})
public class BookingControllerTest extends AbstractControllerTest {
    private static final Logger log = LoggerFactory.getLogger(BookingControllerTest.class);

    @InjectWireMock("movie-service")
    WireMockServer mockMovieService;
    @InjectWireMock("place-service")
    WireMockServer mockPlaceService;
    @InjectWireMock("session-service")
    WireMockServer mockSessionService;
    @InjectWireMock("user-service")
    WireMockServer mockUserService;

    SessionResponse sessionResponseTest;
    SessionResponse sessionResponseOne;
    SessionResponse sessionResponseThree;
    SessionResponse sessionResponseFour;
    SessionResponse sessionResponseFive;
    SessionResponse sessionResponseSeven;

    PlaceResponse placeResponseTwentyForSessionThree;
    PlaceResponse placeResponseTwentyOneForSessionThree;
    PlaceResponse placeResponseTwentyTwoForSessionThree;

    PlaceResponse placeResponseTwentySixForSessionFour;
    PlaceResponse placeResponseTwentySevenForSessionFour;

    PlaceResponse placeResponseThirtySixForSessionFour;
    PlaceResponse placeResponseThirtySevenForSessionFour;
    PlaceResponse placeResponseThirtyEightForSessionFour;

    PlaceResponse placeResponseFortyOneForSessionFour;
    PlaceResponse placeResponseFortyTwoForSessionFour;
    PlaceResponse placeResponseFortyThreeForSessionFour;
    PlaceResponse placeResponseFortyFourForSessionFour;
    PlaceResponse placeResponseFortyFiveForSessionFour;

    PlaceResponse placeResponseFiftyOneForSessionFive;
    PlaceResponse placeResponseFiftyTwoForSessionFive;
    PlaceResponse placeResponseFiftyNineForSessionFive;

    PlaceResponse placeResponseSixtyForSessionFive;
    PlaceResponse placeResponseSixtyOneForSessionFive;
    PlaceResponse placeResponseSixtyTwoForSessionFive;
    PlaceResponse placeResponseSixtyThreeForSessionFive;
    PlaceResponse placeResponseSixtyFourForSessionFive;

    PlaceResponse placeResponseSeventyThreeForSessionFive;

    PlaceResponse placeResponseOneHundredTwelveForSessionSeven;
    PlaceResponse placeResponseOneHundredThirteenForSessionSeven;

    UserResponse userResponseAdmin;
    UserResponse userResponseIvan;
    UserResponse userResponseDima;

    RoleResponse roleResponseAdmin;
    RoleResponse roleResponseManager;
    RoleResponse roleResponseClient;

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
        sessionResponseTest = new SessionResponse(
                999L,
                1196L,
                "2D",
                1,
                OffsetDateTime.now(),
                true
        );
        sessionResponseOne = new SessionResponse(
                1L,
                1L,
                "2D",
                2,
                OffsetDateTime.now().minusDays(1),
                false
        );
        sessionResponseThree = new SessionResponse(
                3L,
                1L,
                "2D",
                1,
                OffsetDateTime.now().plusDays(3),
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
        sessionResponseFive = new SessionResponse(
                5L,
                2L,
                "3D",
                2,
                OffsetDateTime.now().plusDays(2),
                true
        );
        sessionResponseSeven = new SessionResponse(
                7L,
                1L,
                "2D",
                2,
                OffsetDateTime.now().plusDays(4),
                true
        );

        placeResponseTwentyForSessionThree = new PlaceResponse(
                20L,
                3L,
                4,
                20,
                "300.00",
                true
        );
        placeResponseTwentyOneForSessionThree = new PlaceResponse(
                21L,
                3L,
                5,
                21,
                "350.00",
                true
        );
        placeResponseTwentyTwoForSessionThree = new PlaceResponse(
                22L,
                3L,
                5,
                22,
                "350.00",
                true
        );
        placeResponseTwentySixForSessionFour = new PlaceResponse(
                26L,
                4L,
                1,
                1,
                "150.00",
                true
        );
        placeResponseTwentySixForSessionFour = new PlaceResponse(
                27L,
                4L,
                1,
                2,
                "150.00",
                true
        );
        placeResponseThirtySixForSessionFour = new PlaceResponse(
                36L,
                4L,
                3,
                11,
                "250.00",
                false
        );
        placeResponseThirtySevenForSessionFour = new PlaceResponse(
                37L,
                4L,
                3,
                12,
                "250.00",
                false
        );
        placeResponseThirtyEightForSessionFour = new PlaceResponse(
                38L,
                4L,
                3,
                13,
                "250.00",
                false
        );
        placeResponseFortyOneForSessionFour = new PlaceResponse(
                41L,
                4L,
                4,
                16,
                "300.00",
                false
        );
        placeResponseFortyTwoForSessionFour = new PlaceResponse(
                42L,
                4L,
                4,
                17,
                "300.00",
                false
        );
        placeResponseFortyThreeForSessionFour = new PlaceResponse(
                43L,
                4L,
                4,
                18,
                "300.00",
                false
        );
        placeResponseFortyFourForSessionFour = new PlaceResponse(
                44L,
                4L,
                4,
                19,
                "300.00",
                false
        );
        placeResponseFortyFiveForSessionFour = new PlaceResponse(
                45L,
                4L,
                4,
                20,
                "300.00",
                false
        );
        placeResponseFiftyOneForSessionFive = new PlaceResponse(
                51L,
                5L,
                1,
                1,
                "150.00",
                false
        );
        placeResponseFiftyTwoForSessionFive = new PlaceResponse(
                52L,
                5L,
                1,
                2,
                "150.00",
                false
        );
        placeResponseFiftyNineForSessionFive = new PlaceResponse(
                59L,
                5L,
                2,
                9,
                "200.00",
                true
        );
        placeResponseSixtyForSessionFive = new PlaceResponse(
                60L,
                5L,
                2,
                10,
                "200.00",
                true
        );
        placeResponseSixtyOneForSessionFive = new PlaceResponse(
                61L,
                5L,
                3,
                11,
                "250.00",
                false
        );
        placeResponseSixtyTwoForSessionFive = new PlaceResponse(
                62L,
                5L,
                3,
                12,
                "250.00",
                false
        );
        placeResponseSixtyThreeForSessionFive = new PlaceResponse(
                63L,
                5L,
                3,
                13,
                "250.00",
                false
        );
        placeResponseSixtyFourForSessionFive = new PlaceResponse(
                64L,
                5L,
                3,
                14,
                "250.00",
                false
        );
        placeResponseSeventyThreeForSessionFive = new PlaceResponse(
                73L,
                5L,
                5,
                23,
                "350.00",
                false
        );
        placeResponseOneHundredTwelveForSessionSeven = new PlaceResponse(
                112L,
                7L,
                3,
                12,
                "250.00",
                false
        );
        placeResponseOneHundredThirteenForSessionSeven = new PlaceResponse(
                113L,
                7L,
                3,
                13,
                "250.00",
                false
        );

        roleResponseAdmin = new RoleResponse(
                "0edaa6ed-9f09-46e9-a55d-67fa509f2c4f",
                "admin"
        );
        roleResponseClient = new RoleResponse(
                "07a441c8-8512-49c9-b5dd-33a420334d4f",
                "client"
        );
        roleResponseManager = new RoleResponse(
                "75a3f5c6-8180-4552-ac4c-2af7bf0ec348",
                "manager"
        );

        userResponseAdmin = new UserResponse(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                "admin5876",
                "admin5876@example.com",
                true,
                "Admin",
                "Admin",
                "1900-01-01",
                roleResponseAdmin,
                false
        );
        userResponseIvan = new UserResponse(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                "ivan5436",
                "ivan5436@example.com",
                true,
                "Ivan",
                "Petrov",
                "1995-08-12",
                roleResponseManager,
                true
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
    }

    @Test
    @Order(1)
    void getAll_ok() throws JsonProcessingException {
        String sessionResponseTestJson = mapper.writeValueAsString(sessionResponseTest);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("[]").withHeader("Content-Type", "application/json")
                                )
                );
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseTestJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    @Order(2)
    void getAll_some() throws JsonProcessingException {
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("[]").withHeader("Content-Type", "application/json")
                                )
                );
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingSearchRequest request = new BookingSearchRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                4L,
                BookingStatus.PAID,
                LocalDate.now().minusDays(5),
                LocalDate.now().minusDays(4)
        );
        Map<String, String> params = new HashMap<>();
        params.put("userId", request.getUserId());
        params.put("sessionId", request.getSessionId().toString());
        params.put("bookingStatus", request.getBookingStatus().toString());
        params.put("from", request.getFrom().toString());
        params.put("to", request.getTo().toString());

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/x-www-form-urlencoded")
                .formParams(params)
                .when()
                .get("/api/v1/bookings")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1));
    }

    @Test
    @Order(3)
    void getAll_sessionService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис сеансов временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(4)
    void getAll_unauthorized() {
        RestAssured
                .given()
                .when()
                    .get("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(5)
    void getById_ok() throws JsonProcessingException {
        List<PlaceResponse> placeResponsesForSessionFour = List.of(
                placeResponseThirtySixForSessionFour,
                placeResponseThirtySevenForSessionFour,
                placeResponseThirtyEightForSessionFour
        );
        String placeResponsesForSessionFourJson = mapper.writeValueAsString(placeResponsesForSessionFour);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFourJson).withHeader("Content-Type", "application/json")
                                )
                );
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .pathParam("id", 2)
                .when()
                    .get("/api/v1/bookings/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(2))
                    .body("userId", Matchers.equalTo("14b8135e-4a62-4104-ac6a-26eefaeeef17"))
                    .body("session.id", Matchers.equalTo(4))
                    .body("session.movieId", Matchers.equalTo(2))
                    .body("session.movieFormat", Matchers.equalTo("3D"))
                    .body("session.hall", Matchers.equalTo(3))
                    .body("session.dateTime", Matchers.notNullValue())
                    .body("session.available", Matchers.equalTo(true))
                    .body("places", Matchers.hasSize(3))
                    .body("places[0].id", Matchers.equalTo(36))
                    .body("places[0].sessionId", Matchers.equalTo(4))
                    .body("places[0].row", Matchers.equalTo(3))
                    .body("places[0].number", Matchers.equalTo(11))
                    .body("places[0].price", Matchers.equalTo("250.00"))
                    .body("places[0].available", Matchers.equalTo(false))
                    .body("places[1].id", Matchers.equalTo(37))
                    .body("places[1].sessionId", Matchers.equalTo(4))
                    .body("places[1].row", Matchers.equalTo(3))
                    .body("places[1].number", Matchers.equalTo(12))
                    .body("places[1].price", Matchers.equalTo("250.00"))
                    .body("places[1].available", Matchers.equalTo(false))
                    .body("places[2].id", Matchers.equalTo(38))
                    .body("places[2].sessionId", Matchers.equalTo(4))
                    .body("places[2].row", Matchers.equalTo(3))
                    .body("places[2].number", Matchers.equalTo(13))
                    .body("places[2].price", Matchers.equalTo("250.00"))
                    .body("places[2].available", Matchers.equalTo(false))
                    .body("status", Matchers.equalTo("Paid"))
                    .body("createdDate", Matchers.notNullValue())
                    .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(6)
    void getById_clientRole_ok() throws JsonProcessingException {
        List<PlaceResponse> placeResponsesForSessionFive = List.of(
                placeResponseFiftyNineForSessionFive,
                placeResponseSixtyForSessionFive
        );
        String placeResponsesForSessionFiveJson = mapper.writeValueAsString(placeResponsesForSessionFive);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .pathParam("id", 8)
                .when()
                    .get("/api/v1/bookings/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(8))
                    .body("userId", Matchers.equalTo("3c59a7b2-4cff-49b6-a654-3145ecdab36b"))
                    .body("session.id", Matchers.equalTo(5))
                    .body("session.movieId", Matchers.equalTo(2))
                    .body("session.movieFormat", Matchers.equalTo("3D"))
                    .body("session.hall", Matchers.equalTo(2))
                    .body("session.dateTime", Matchers.notNullValue())
                    .body("session.available", Matchers.equalTo(true))
                    .body("places", Matchers.hasSize(2))
                    .body("places[0].id", Matchers.equalTo(59))
                    .body("places[0].sessionId", Matchers.equalTo(5))
                    .body("places[0].row", Matchers.equalTo(2))
                    .body("places[0].number", Matchers.equalTo(9))
                    .body("places[0].price", Matchers.equalTo("200.00"))
                    .body("places[0].available", Matchers.equalTo(true))
                    .body("places[1].id", Matchers.equalTo(60))
                    .body("places[1].sessionId", Matchers.equalTo(5))
                    .body("places[1].row", Matchers.equalTo(2))
                    .body("places[1].number", Matchers.equalTo(10))
                    .body("places[1].price", Matchers.equalTo("200.00"))
                    .body("places[1].available", Matchers.equalTo(true))
                    .body("status", Matchers.equalTo("Canceled"))
                    .body("createdDate", Matchers.notNullValue())
                    .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(7)
    void getById_entityNotFindException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .pathParam("id", 11999)
                .when()
                    .get("/api/v1/bookings/{id}")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Бронирование с идентификатором 11999 не найдено"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(8)
    void getById_clientRole_entityNotFindException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 2655)
                .when()
                .get("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Бронирование с идентификатором 2655 не найдено"))
                .body("dateTime", Matchers.notNullValue());

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                    .pathParam("id", 2)
                .when()
                    .get("/api/v1/bookings/{id}")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Бронирование с идентификатором 2 для пользователя 3c59a7b2-4cff-49b6-a654-3145ecdab36b не найдено"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(9)
    void getById_unauthorized() {
        RestAssured
                .given()
                    .pathParam("id", 2)
                .when()
                    .get("/api/v1/bookings/{id}")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(10)
    void create_ok() throws JsonProcessingException {
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places//search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseAdminJson = mapper.writeValueAsString(userResponseAdmin);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseAdminJson).withHeader("Content-Type", "application/json")
                                )
                );
        List<PlaceResponse> placeResponsesForSessionFive = new ArrayList<>();
        placeResponsesForSessionFive.add(placeResponseFiftyOneForSessionFive);
        placeResponsesForSessionFive.add(placeResponseFiftyTwoForSessionFive);
        String placeResponsesForSessionFiveJson = mapper.writeValueAsString(placeResponsesForSessionFive);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                5L,
                Set.of(51L, 52L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", Matchers.notNullValue())
                    .body("userId", Matchers.equalTo("3ca5d554-4102-4fa5-bc54-c355502b1fe5"))
                    .body("session.id", Matchers.equalTo(5))
                    .body("session.movieId", Matchers.equalTo(2))
                    .body("session.movieFormat", Matchers.equalTo("3D"))
                    .body("session.hall", Matchers.equalTo(2))
                    .body("session.dateTime", Matchers.notNullValue())
                    .body("session.available", Matchers.equalTo(true))
                    .body("places", Matchers.hasSize(2))
                    .body("places[0].id", Matchers.equalTo(51))
                    .body("places[0].sessionId", Matchers.equalTo(5))
                    .body("places[0].row", Matchers.equalTo(1))
                    .body("places[0].number", Matchers.equalTo(1))
                    .body("places[0].price", Matchers.equalTo("150.00"))
                    .body("places[0].available", Matchers.equalTo(false))
                    .body("places[1].id", Matchers.equalTo(52))
                    .body("places[1].sessionId", Matchers.equalTo(5))
                    .body("places[1].row", Matchers.equalTo(1))
                    .body("places[1].number", Matchers.equalTo(2))
                    .body("places[1].price", Matchers.equalTo("150.00"))
                    .body("places[1].available", Matchers.equalTo(false))
                    .body("status", Matchers.equalTo("Created"))
                    .body("createdDate", Matchers.notNullValue())
                    .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(11)
    void create_clientRole_ok() throws JsonProcessingException {
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
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
        List<PlaceResponse> placeResponsesForSessionFive = new ArrayList<>();
        placeResponsesForSessionFive.add(placeResponseSixtyOneForSessionFive);
        placeResponsesForSessionFive.add(placeResponseSixtyTwoForSessionFive);
        String placeResponsesForSessionFiveJson = mapper.writeValueAsString(placeResponsesForSessionFive);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFiveJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        BookingRequest request = new BookingRequest(
                5L,
                Set.of(61L, 62L),
                BookingStatus.PAID
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", Matchers.notNullValue())
                    .body("userId", Matchers.equalTo("3c59a7b2-4cff-49b6-a654-3145ecdab36b"))
                    .body("session.id", Matchers.equalTo(5))
                    .body("session.movieId", Matchers.equalTo(2))
                    .body("session.movieFormat", Matchers.equalTo("3D"))
                    .body("session.hall", Matchers.equalTo(2))
                    .body("session.dateTime", Matchers.notNullValue())
                    .body("session.available", Matchers.equalTo(true))
                    .body("places", Matchers.hasSize(2))
                    .body("places[0].id", Matchers.equalTo(61))
                    .body("places[0].sessionId", Matchers.equalTo(5))
                    .body("places[0].row", Matchers.equalTo(3))
                    .body("places[0].number", Matchers.equalTo(11))
                    .body("places[0].price", Matchers.equalTo("250.00"))
                    .body("places[0].available", Matchers.equalTo(false))
                    .body("places[1].id", Matchers.equalTo(62))
                    .body("places[1].sessionId", Matchers.equalTo(5))
                    .body("places[1].row", Matchers.equalTo(3))
                    .body("places[1].number", Matchers.equalTo(12))
                    .body("places[1].price", Matchers.equalTo("250.00"))
                    .body("places[1].available", Matchers.equalTo(false))
                    .body("status", Matchers.equalTo("Paid"))
                    .body("createdDate", Matchers.notNullValue())
                    .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(12)
    void create_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "16ea6c4c-68c8-4ef8-b503-0149c97f59e765554",
                null,
                null,
                null
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.userId", Matchers.equalTo("Идентификатор пользователя не может содержать более 36 символов"))
                    .body("fields.sessionId", Matchers.equalTo("Идентификатор сеанса не может быть пустым"))
                    .body("fields.placeIds", Matchers.equalTo("Список идентификаторов мест должны содержать хотя бы один элемент"))
                    .body("fields.bookingStatus", Matchers.equalTo("Статус бронирования не может быть пустым"))
                    .body("dateTime", Matchers.notNullValue());

        request = new BookingRequest(
                0L,
                Set.of(1L, 2L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Ошибка валидации"))
                    .body("fields.sessionId", Matchers.equalTo("Минимальное значение идентификатора сеанса 1"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(13)
    void create_badRequestException_statusCancelled() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                4L,
                Set.of(1L, 2L, 3L),
                BookingStatus.CANCELED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Невозможно создать отмененную бронь"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(14)
    void create_badRequestException_sessionIsNotAvailable() throws JsonProcessingException {
        String sessionResponseOneJson = mapper.writeValueAsString(sessionResponseOne);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseOneJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                1L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Сеанс с идентификатором 1 недоступен для бронирования"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(15)
    void create_badRequestException_sessionHasPassed() throws JsonProcessingException {
        OffsetDateTime dateTime = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);

        SessionResponse sessionResponseNineHundredNinetySeven = new SessionResponse(
                997L,
                1L,
                "3D",
                1,
                dateTime,
                true
        );
        String sessionResponseNineHundredNinetySevenJson = mapper.writeValueAsString(sessionResponseNineHundredNinetySeven);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseNineHundredNinetySevenJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("98").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                997L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );

        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", Matchers.equalTo(400))
                    .body("message", Matchers.equalTo("Бронирование мест невозможно, т.к. сеанс %s в зале 1 закончен".formatted(DateUtil.formatDate(dateTime))))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(16)
    void create_entityAlreadyExistsException_placeNotEqualsSessionBySessionIdAndIds() throws JsonProcessingException {
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("81").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                5L,
                Set.of(65L, 66L, 81L),
                BookingStatus.PAID
        );

        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Место с идентификатором 81 не относится к сеансу с идентификатором 5"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(17)
    void create_entityAlreadyExistsException_placeBySessionIdAndIdsAndAvailableFalse() throws JsonProcessingException {
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("45").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                4L,
                Set.of(45L, 46L, 47L),
                BookingStatus.PAID
        );

        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/bookings")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Место с идентификатором 45 занято"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(18)
    void create_sessionService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                5L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис сеансов временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(19)
    void create_movieService_unavailable() throws JsonProcessingException {
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                5L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис фильмов временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(20)
    void create_userService_unavailable() throws JsonProcessingException {
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        List<PlaceResponse> placeResponsesForSessionFive = new ArrayList<>();
        placeResponsesForSessionFive.add(placeResponseSixtyThreeForSessionFive);
        placeResponsesForSessionFive.add(placeResponseSixtyFourForSessionFive);
        String placeResponsesForSessionFiveJson = mapper.writeValueAsString(placeResponsesForSessionFive);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFiveJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                5L,
                Set.of(63L, 64L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(500)
                    .body("code", Matchers.equalTo(500))
                    .body("message", Matchers.equalTo("Сервис пользователей временно недоступен, повторите попытку позже!"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(21)
    void create_userService_unauthorized() {
        BookingRequest request = new BookingRequest(
                5L,
                Set.of(63L, 64L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                    .body(request)
                    .contentType("application/json")
                .when()
                    .post("/api/v1/bookings")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    @Order(22)
    void update_ok_replacePlaces() throws JsonProcessingException {
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseIvanJson = mapper.writeValueAsString(userResponseIvan);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseIvanJson).withHeader("Content-Type", "application/json")
                                )
                );
        List<PlaceResponse> placeResponsesForSessionFive = new ArrayList<>();
        placeResponsesForSessionFive.add(placeResponseSeventyThreeForSessionFive);
        String placeResponsesForSessionFiveJson = mapper.writeValueAsString(placeResponsesForSessionFive);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFiveJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                5L,
                Set.of(73L),
                BookingStatus.PAID
        );
        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("userId", Matchers.equalTo("14b8135e-4a62-4104-ac6a-26eefaeeef17"))
                .body("session.id", Matchers.equalTo(5))
                .body("session.movieId", Matchers.equalTo(2))
                .body("session.movieFormat", Matchers.equalTo("3D"))
                .body("session.hall", Matchers.equalTo(2))
                .body("session.dateTime", Matchers.notNullValue())
                .body("session.available", Matchers.equalTo(true))
                .body("places", Matchers.hasSize(1))
                .body("places[0].id", Matchers.equalTo(73))
                .body("places[0].sessionId", Matchers.equalTo(5))
                .body("places[0].row", Matchers.equalTo(5))
                .body("places[0].number", Matchers.equalTo(23))
                .body("places[0].price", Matchers.equalTo("350.00"))
                .body("places[0].available", Matchers.equalTo(false))
                .body("status", Matchers.equalTo("Paid"))
                .body("createdDate", Matchers.notNullValue())
                .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(23)
    void update_ok_statusCanceled() throws JsonProcessingException {
        String sessionResponseThreeJson = mapper.writeValueAsString(sessionResponseThree);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseThreeJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        List<PlaceResponse> placeResponsesForSessionThree = new ArrayList<>();
        placeResponsesForSessionThree.add(placeResponseTwentyForSessionThree);
        placeResponsesForSessionThree.add(placeResponseTwentyOneForSessionThree);
        placeResponsesForSessionThree.add(placeResponseTwentyTwoForSessionThree);
        String placeResponsesForSessionThreeJson = mapper.writeValueAsString(placeResponsesForSessionThree);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionThreeJson).withHeader("Content-Type", "application/json")
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

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                3L,
                Set.of(20L, 21L, 22L),
                BookingStatus.CANCELED
        );
        RestAssured
                .given()
                .pathParam("id", 7)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("userId", Matchers.equalTo("3c59a7b2-4cff-49b6-a654-3145ecdab36b"))
                .body("session.id", Matchers.equalTo(3))
                .body("session.movieId", Matchers.equalTo(1))
                .body("session.movieFormat", Matchers.equalTo("2D"))
                .body("session.hall", Matchers.equalTo(1))
                .body("session.dateTime", Matchers.notNullValue())
                .body("session.available", Matchers.equalTo(true))
                .body("places", Matchers.hasSize(3))
                .body("places[0].id", Matchers.equalTo(20))
                .body("places[0].sessionId", Matchers.equalTo(3))
                .body("places[0].row", Matchers.equalTo(4))
                .body("places[0].number", Matchers.equalTo(20))
                .body("places[0].price", Matchers.equalTo("300.00"))
                .body("places[0].available", Matchers.equalTo(true))
                .body("places[1].id", Matchers.equalTo(21))
                .body("places[1].sessionId", Matchers.equalTo(3))
                .body("places[1].row", Matchers.equalTo(5))
                .body("places[1].number", Matchers.equalTo(21))
                .body("places[1].price", Matchers.equalTo("350.00"))
                .body("places[1].available", Matchers.equalTo(true))
                .body("places[2].id", Matchers.equalTo(22))
                .body("places[2].sessionId", Matchers.equalTo(3))
                .body("places[2].row", Matchers.equalTo(5))
                .body("places[2].number", Matchers.equalTo(22))
                .body("places[2].price", Matchers.equalTo("350.00"))
                .body("places[2].available", Matchers.equalTo(true))
                .body("status", Matchers.equalTo("Canceled"))
                .body("createdDate", Matchers.notNullValue())
                .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(24)
    void update_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "16ea6c4c-68c8-4ef8-b503-0149c97f59e765554",
                null,
                null,
                null
        );

        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.userId", Matchers.equalTo("Идентификатор пользователя не может содержать более 36 символов"))
                .body("fields.sessionId", Matchers.equalTo("Идентификатор сеанса не может быть пустым"))
                .body("fields.placeIds", Matchers.equalTo("Список идентификаторов мест должны содержать хотя бы один элемент"))
                .body("fields.bookingStatus", Matchers.equalTo("Статус бронирования не может быть пустым"))
                .body("dateTime", Matchers.notNullValue());

        request = new BookingRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                0L,
                Set.of(1L, 2L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.sessionId", Matchers.equalTo("Минимальное значение идентификатора сеанса 1"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(25)
    void update_badRequestException_userIdIsEmpty() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                4L,
                Set.of(26L, 27L),
                BookingStatus.PAID
        );
        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Идентификатор пользователя не может быть пустым"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(26)
    void update_badRequestException_bookingAndStatusCanceledForUpdate() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                3L,
                Set.of(24L),
                BookingStatus.PAID
        );

        RestAssured
                .given()
                .pathParam("id", 8)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Невозможно обновить отмененную бронь"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(27)
    void update_entityNotFoundException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                3L,
                Set.of(24L),
                BookingStatus.PAID
        );

        RestAssured
                .given()
                .pathParam("id", 199)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Бронирование с идентификатором 199 не найдено"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(28)
    void update_badRequestException_sessionIsNotAvailable() throws JsonProcessingException {
        String sessionResponseOneJson = mapper.writeValueAsString(sessionResponseOne);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseOneJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                1L,
                Set.of(73L),
                BookingStatus.PAID
        );
        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Сеанс с идентификатором 1 недоступен для бронирования"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(29)
    void update_badRequestException_sessionHasPassed() throws JsonProcessingException {
        OffsetDateTime dateTime = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);

        SessionResponse sessionResponseNineHundredNinetySeven = new SessionResponse(
                997L,
                1L,
                "3D",
                1,
                dateTime,
                true
        );
        String sessionResponseNineHundredNinetySevenJson = mapper.writeValueAsString(sessionResponseNineHundredNinetySeven);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseNineHundredNinetySevenJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("98").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                997L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );

        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Бронирование мест невозможно, т.к. сеанс %s в зале 1 закончен".formatted(DateUtil.formatDate(dateTime))))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(30)
    void update_entityAlreadyExistsException_placeNotEqualsSessionBySessionIdAndIds() throws JsonProcessingException {
        String sessionResponseFiveJson = mapper.writeValueAsString(sessionResponseFive);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFiveJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("101").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                5L,
                Set.of(101L, 102L, 103L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Место с идентификатором 101 не относится к сеансу с идентификатором 5"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(31)
    void update_entityAlreadyExistsException_placeBySessionIdAndIdsAndAvailableFalse() throws JsonProcessingException {
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("45").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                4L,
                Set.of(45L, 46L, 47L),
                BookingStatus.PAID
        );

        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Место с идентификатором 45 занято"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(32)
    void update_sessionService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                5L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис сеансов временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(33)
    void update_movieService_unavailable() throws JsonProcessingException {
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingRequest request = new BookingRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                4L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис фильмов временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(34)
    void update_userService_unavailable() throws JsonProcessingException {
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/[0-9]+/duration"))
                                .willReturn(
                                        WireMock.ok("114").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session-not-equals/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/places/search/session/[0-9]+/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok("0").withHeader("Content-Type", "application/json")
                                )
                );
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        List<PlaceResponse> placeResponsesForSessionFour = new ArrayList<>();
        placeResponsesForSessionFour.add(placeResponseTwentySixForSessionFour);
        placeResponsesForSessionFour.add(placeResponseTwentySevenForSessionFour);
        String placeResponsesForSessionFiveJson = mapper.writeValueAsString(placeResponsesForSessionFour);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFiveJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingRequest request = new BookingRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                4L,
                Set.of(26L, 27L),
                BookingStatus.PAID
        );
        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис пользователей временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(35)
    void update_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        BookingRequest request = new BookingRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                4L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(36)
    void update_unauthorized() {
        BookingRequest request = new BookingRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                4L,
                Set.of(65L, 66L, 67L),
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .when()
                .put("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(37)
    void updateStatus_ok() throws JsonProcessingException {
        placeResponseThirtySixForSessionFour = new PlaceResponse(
                36L,
                4L,
                3,
                11,
                "250.00",
                true
        );
        placeResponseThirtySevenForSessionFour = new PlaceResponse(
                37L,
                4L,
                3,
                12,
                "250.00",
                true
        );
        placeResponseThirtyEightForSessionFour = new PlaceResponse(
                38L,
                4L,
                3,
                13,
                "250.00",
                true
        );

        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        List<PlaceResponse> placeResponsesForSessionFour = new ArrayList<>();
        placeResponsesForSessionFour.add(placeResponseThirtySixForSessionFour);
        placeResponsesForSessionFour.add(placeResponseThirtySevenForSessionFour);
        placeResponsesForSessionFour.add(placeResponseThirtyEightForSessionFour);
        String placeResponsesForSessionThreeJson = mapper.writeValueAsString(placeResponsesForSessionFour);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionThreeJson).withHeader("Content-Type", "application/json")
                                )
                );
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseIvanJson = mapper.writeValueAsString(userResponseIvan);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseIvanJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                BookingStatus.CANCELED
        );
        RestAssured
                .given()
                .pathParam("id", 2)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("userId", Matchers.equalTo("14b8135e-4a62-4104-ac6a-26eefaeeef17"))
                .body("session.id", Matchers.equalTo(4))
                .body("session.movieId", Matchers.equalTo(2))
                .body("session.movieFormat", Matchers.equalTo("3D"))
                .body("session.hall", Matchers.equalTo(3))
                .body("session.dateTime", Matchers.notNullValue())
                .body("session.available", Matchers.equalTo(true))
                .body("places", Matchers.hasSize(3))
                .body("places[0].id", Matchers.equalTo(36))
                .body("places[0].sessionId", Matchers.equalTo(4))
                .body("places[0].row", Matchers.equalTo(3))
                .body("places[0].number", Matchers.equalTo(11))
                .body("places[0].price", Matchers.equalTo("250.00"))
                .body("places[0].available", Matchers.equalTo(true))
                .body("places[1].id", Matchers.equalTo(37))
                .body("places[1].sessionId", Matchers.equalTo(4))
                .body("places[1].row", Matchers.equalTo(3))
                .body("places[1].number", Matchers.equalTo(12))
                .body("places[1].price", Matchers.equalTo("250.00"))
                .body("places[1].available", Matchers.equalTo(true))
                .body("places[2].id", Matchers.equalTo(38))
                .body("places[2].sessionId", Matchers.equalTo(4))
                .body("places[2].row", Matchers.equalTo(3))
                .body("places[2].number", Matchers.equalTo(13))
                .body("places[2].price", Matchers.equalTo("250.00"))
                .body("places[2].available", Matchers.equalTo(true))
                .body("status", Matchers.equalTo("Canceled"))
                .body("createdDate", Matchers.notNullValue())
                .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(38)
    void updateStatus_clientRole_ok() throws JsonProcessingException {
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        List<PlaceResponse> placeResponsesForSessionFour = new ArrayList<>();
        placeResponsesForSessionFour.add(placeResponseFortyOneForSessionFour);
        placeResponsesForSessionFour.add(placeResponseFortyTwoForSessionFour);
        placeResponsesForSessionFour.add(placeResponseFortyThreeForSessionFour);
        placeResponsesForSessionFour.add(placeResponseFortyFourForSessionFour);
        placeResponsesForSessionFour.add(placeResponseFortyFiveForSessionFour);
        String placeResponsesForSessionFourJson = mapper.writeValueAsString(placeResponsesForSessionFour);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionFourJson).withHeader("Content-Type", "application/json")
                                )
                );
        String sessionResponseFourJson = mapper.writeValueAsString(sessionResponseFour);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseFourJson).withHeader("Content-Type", "application/json")
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

        BookingStatusRequest request = new BookingStatusRequest(
                BookingStatus.PAID
        );
        RestAssured
                .given()
                .pathParam("id", 3)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("userId", Matchers.equalTo("3c59a7b2-4cff-49b6-a654-3145ecdab36b"))
                .body("session.id", Matchers.equalTo(4))
                .body("session.movieId", Matchers.equalTo(2))
                .body("session.movieFormat", Matchers.equalTo("3D"))
                .body("session.hall", Matchers.equalTo(3))
                .body("session.dateTime", Matchers.notNullValue())
                .body("session.available", Matchers.equalTo(true))
                .body("places", Matchers.hasSize(5))
                .body("places[0].id", Matchers.equalTo(41))
                .body("places[0].sessionId", Matchers.equalTo(4))
                .body("places[0].row", Matchers.equalTo(4))
                .body("places[0].number", Matchers.equalTo(16))
                .body("places[0].price", Matchers.equalTo("300.00"))
                .body("places[0].available", Matchers.equalTo(false))
                .body("places[1].id", Matchers.equalTo(42))
                .body("places[1].sessionId", Matchers.equalTo(4))
                .body("places[1].row", Matchers.equalTo(4))
                .body("places[1].number", Matchers.equalTo(17))
                .body("places[1].price", Matchers.equalTo("300.00"))
                .body("places[1].available", Matchers.equalTo(false))
                .body("places[2].id", Matchers.equalTo(43))
                .body("places[2].sessionId", Matchers.equalTo(4))
                .body("places[2].row", Matchers.equalTo(4))
                .body("places[2].number", Matchers.equalTo(18))
                .body("places[2].price", Matchers.equalTo("300.00"))
                .body("places[2].available", Matchers.equalTo(false))
                .body("places[3].id", Matchers.equalTo(44))
                .body("places[3].sessionId", Matchers.equalTo(4))
                .body("places[3].row", Matchers.equalTo(4))
                .body("places[3].number", Matchers.equalTo(19))
                .body("places[3].price", Matchers.equalTo("300.00"))
                .body("places[3].available", Matchers.equalTo(false))
                .body("places[4].id", Matchers.equalTo(45))
                .body("places[4].sessionId", Matchers.equalTo(4))
                .body("places[4].row", Matchers.equalTo(4))
                .body("places[4].number", Matchers.equalTo(20))
                .body("places[4].price", Matchers.equalTo("300.00"))
                .body("places[4].available", Matchers.equalTo(false))
                .body("status", Matchers.equalTo("Paid"))
                .body("createdDate", Matchers.notNullValue())
                .body("updatedDate", Matchers.notNullValue());
    }

    @Test
    @Order(39)
    void updateStatus_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                "cecdbfd0-60cd-41af-b3f7-32b0d3beff907777",
                null
        );
        RestAssured
                .given()
                .pathParam("id", 7)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.userId", Matchers.equalTo("Идентификатор пользователя не может содержать более 36 символов"))
                .body("fields.bookingStatus", Matchers.equalTo("Статус бронирования не может быть пустым"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(40)
    void updateStatus_entityNotFoundException_notExistsById() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                BookingStatus.PAID
        );

        RestAssured
                .given()
                .pathParam("id", 999)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Бронирование с идентификатором 999 не найдено"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(41)
    void updateStatus_entityNotFoundException_notExistsByIdAndUserId() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                "3c59a7b2-4cff-49b6-a654-3145ecdab36b",
                BookingStatus.PAID
        );

        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Бронирование с идентификатором 4 для пользователя 3c59a7b2-4cff-49b6-a654-3145ecdab36b не найдено"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(42)
    void updateStatus_badRequestException_idAndBookingStatusCanceled() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                BookingStatus.CANCELED
        );

        RestAssured
                .given()
                .pathParam("id", 1)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Невозможно обновить отмененную бронь"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(43)
    void updateStatus_entityAlreadyExistsException_existsByIdAndBookingStatus() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                BookingStatus.PAID
        );

        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Статус бронирования Paid у брони с идентификатором 5 уже существует"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(44)
    void updateStatus_sessionService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис сеансов временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(45)
    void updateStatus_userService_unavailable() throws JsonProcessingException {
        List<PlaceResponse> placeResponsesForSessionSeven = List.of(
                placeResponseOneHundredTwelveForSessionSeven,
                placeResponseOneHundredThirteenForSessionSeven
        );
        String placeResponsesForSessionSevenJson = mapper.writeValueAsString(placeResponsesForSessionSeven);
        mockPlaceService
                .stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/api/v1/places/search/ids"))
                                .withQueryParam("values", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok(placeResponsesForSessionSevenJson).withHeader("Content-Type", "application/json")
                                )
                );
        String sessionResponseSevenJson = mapper.writeValueAsString(sessionResponseSeven);
        mockSessionService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/sessions/[^/]+"))
                                .willReturn(
                                        WireMock.ok(sessionResponseSevenJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        BookingStatusRequest request = new BookingStatusRequest(
                "3ca5d554-4102-4fa5-bc54-c355502b1fe5",
                BookingStatus.PAID
        );
        RestAssured
                .given()
                .pathParam("id", 9)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис пользователей временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(46)
    void updateStatus_unauthorized() {
        BookingStatusRequest request = new BookingStatusRequest(
                "14b8135e-4a62-4104-ac6a-26eefaeeef17",
                BookingStatus.CREATED
        );
        RestAssured
                .given()
                .pathParam("id", 2)
                .body(request)
                .contentType("application/json")
                .when()
                .patch("/api/v1/bookings/{id}/status")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(47)
    void deleteById_ok() {
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 1)
                .when()
                .delete("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(48)
    void deleteById_statusCanceled_ok() throws JsonProcessingException {
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );
        String userResponseAdminJson = mapper.writeValueAsString(userResponseAdmin);
        mockUserService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/users/[^/]+"))
                                .willReturn(
                                        WireMock.ok(userResponseAdminJson).withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 7)
                .when()
                .delete("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(49)
    void deleteById_entityNotFoundException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 11999)
                .when()
                .delete("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Бронирование с идентификатором 11999 не найдено"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(50)
    void deleteById_sessionService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 9)
                .when()
                .delete("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис сеансов временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(51)
    void deleteById_userService_unavailable() {
        mockPlaceService
                .stubFor(
                        WireMock.patch(WireMock.urlPathEqualTo("/api/v1/places/ids/update/available-places"))
                                .withQueryParam("sessionId", WireMock.matching(".*"))
                                .withQueryParam("ids", WireMock.matching(".*"))
                                .withQueryParam("available", WireMock.matching(".*"))
                                .willReturn(
                                        WireMock.ok().withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 9)
                .when()
                .delete("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис пользователей временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(52)
    void deleteById_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 5)
                .when()
                .delete("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(53)
    void deleteById_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", 5)
                .when()
                .delete("/api/v1/bookings/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(54)
    void existsByIdAndUserId_true() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 2)
                .queryParam("userId", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .when()
                .get("/api/v1/bookings/{id}/user")
                .then()
                .log().all()
                .statusCode(200)
                .body("$", Matchers.equalTo(true));
    }

    @Test
    @Order(55)
    void existsByIdAndUserId_false() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", 11999)
                .queryParam("userId", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .when()
                .get("/api/v1/bookings/{id}/user")
                .then()
                .log().all()
                .statusCode(200)
                .body("$", Matchers.equalTo(false));
    }

    @Test
    @Order(56)
    void existsByIdAndUserId_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", 2)
                .queryParam("userId", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .when()
                .get("/api/v1/bookings/{id}/user")
                .then()
                .log().all()
                .statusCode(401);
    }
}
