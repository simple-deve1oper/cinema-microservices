package dev.session.controller;

import dev.library.domain.session.dto.PlaceRequest;
import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import dev.session.entity.Place;
import dev.session.repository.PlaceRepository;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlaceControllerTest extends AbstractControllerTest {
    private static final Logger log = LoggerFactory.getLogger(PlaceControllerTest.class);

    @Autowired
    private PlaceRepository placeRepository;

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

    @Test
    @Order(1)
    void getAll_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    @Order(2)
    void getAll_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(3)
    void getAll_unauthorized() {
        RestAssured
                .given()
                .when()
                .get("/api/v1/places")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(2)
    void getAllBySession_Id_ok() {
        RestAssured
                .given()
                .pathParam("session-id", 4)
                .when()
                .get("/api/v1/places/session/{session-id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(25));
    }

    @Test
    @Order(3)
    void getAllBySession_Id_empty() {
        RestAssured
                .given()
                .pathParam("session-id", 11999)
                .when()
                .get("/api/v1/places/session/{session-id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(0));
    }

    @Test
    @Order(4)
    void getAllByIds_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        Set<Long> values = Set.of(1L, 15L, 30L);
        RestAssured
                .given()
                .queryParam("values", values)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places/search/ids")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(3))
                .body("[0].id", Matchers.equalTo(1))
                .body("[1].id", Matchers.equalTo(15))
                .body("[2].id", Matchers.equalTo(30));
    }

    @Test
    @Order(5)
    void getAllByIds_some() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(16L, 77L, 11999L, 23078L, 45556L);
        RestAssured
                .given()
                .queryParam("values", values)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places/search/ids")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(2))
                .body("[0].id", Matchers.equalTo(16))
                .body("[1].id", Matchers.equalTo(77));
    }

    @Test
    @Order(6)
    void getAllByIds_empty() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        Set<Long> values = Set.of(970L, 998L, 11999L, 23078L, 45556L);
        RestAssured
                .given()
                .queryParam("values", values)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places/search/ids")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(0));
    }

    @Test
    @Order(7)
    void getAllByIds_unauthorized() {
        Set<Long> values = Set.of(11L, 21L, 26L, 31L, 55L);
        RestAssured
                .given()
                .queryParam("values", values)
                .when()
                .get("/api/v1/places/search/ids")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(8)
    void getById_ok() {
        RestAssured
                .given()
                .pathParam("id", 15)
                .when()
                .get("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.equalTo(15))
                .body("sessionId", Matchers.equalTo(3))
                .body("row", Matchers.equalTo(3))
                .body("number", Matchers.equalTo(15))
                .body("price", Matchers.equalTo("250.00"))
                .body("available", Matchers.equalTo(true));
    }

    @Test
    @Order(9)
    void getById_entityNotFoundException() {
        RestAssured
                .given()
                .pathParam("id", 999)
                .when()
                .get("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Место с идентификатором 999 не найдено"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(11)
    void getPlaceBySessionIdAndIdsAndAvailable_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        Set<Long> values = Set.of(10L, 15L, 45L);
        RestAssured
                .given()
                .pathParam("session-id", 3)
                .queryParam("values", values)
                .queryParam("available", true)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places/search/session/{session-id}/ids")
                .then()
                .log().all()
                .statusCode(200)
                .body("$", Matchers.equalTo(10));
    }

    @Test
    @Order(12)
    void getPlaceBySessionIdAndIdsAndAvailable_zero() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(105L, 153L, 101L);
        RestAssured
                .given()
                .pathParam("session-id", 3)
                .queryParam("values", values)
                .queryParam("available", true)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places/search/session/{session-id}/ids")
                .then()
                .log().all()
                .statusCode(200)
                .body("$", Matchers.equalTo(0));
    }

    @Test
    @Order(13)
    void getPlaceBySessionIdAndIdsAndAvailable_unauthorized() {
        Set<Long> values = Set.of(1L, 2L, 3L);
        RestAssured
                .given()
                .pathParam("session-id", 3)
                .queryParam("values", values)
                .queryParam("available", true)
                .when()
                .get("/api/v1/places/search/session/{session-id}/ids")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(14)
    void getPlaceNotEqualsSessionBySessionIdAndIds_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        Set<Long> values = Set.of(106L, 15L, 101L, 25L);
        RestAssured
                .given()
                .pathParam("session-id", 3)
                .queryParam("values", values)
                .queryParam("available", true)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places/search/session-not-equals/{session-id}/ids")
                .then()
                .log().all()
                .statusCode(200)
                .body("$", Matchers.equalTo(101));
    }

    @Test
    @Order(15)
    void getPlaceNotEqualsSessionBySessionIdAndIds_empty() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        Set<Long> values = Set.of(31L, 45L, 27L, 26L);
        RestAssured
                .given()
                .pathParam("session-id", 4)
                .queryParam("values", values)
                .queryParam("available", true)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/places/search/session-not-equals/{session-id}/ids")
                .then()
                .log().all()
                .statusCode(200)
                .body("$", Matchers.equalTo(0));
    }

    @Test
    @Order(16)
    void getPlaceNotEqualsSessionBySessionIdAndIds_unauthorized() {
        Set<Long> values = Set.of(31L, 15L, 27L, 26L);
        RestAssured
                .given()
                .pathParam("session-id", 4)
                .queryParam("values", values)
                .queryParam("available", true)
                .when()
                .get("/api/v1/places/search/session-not-equals/{session-id}/ids")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(17)
    void create_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        PlaceRequest request = new PlaceRequest(
                8L,
                3,
                15,
                BigDecimal.valueOf(250.00),
                false
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/places")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", Matchers.equalTo(126))
                .body("sessionId", Matchers.equalTo(8))
                .body("row", Matchers.equalTo(3))
                .body("number", Matchers.equalTo(15))
                .body("price", Matchers.equalTo("250.00"))
                .body("available", Matchers.equalTo(false));
    }

    @Test
    @Order(18)
    void create_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        PlaceRequest request = new PlaceRequest(
                null,
                null,
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
                .post("/api/v1/places")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.sessionId", Matchers.equalTo("Идентификатор сеанса не может быть пустым"))
                .body("fields.row", Matchers.equalTo("Номер ряда в зале не может быть пустым"))
                .body("fields.number", Matchers.equalTo("Номер места в зале не может быть пустым"))
                .body("fields.price", Matchers.equalTo("Цена за место не может быть пустой"))
                .body("fields.available", Matchers.equalTo("Доступность места не может быть пустым"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(19)
    void create_entityAlreadyExistsException_existsBySessionIdAndRowAndNumber() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        PlaceRequest request = new PlaceRequest(
                4L,
                1,
                1,
                BigDecimal.valueOf(250.00),
                true
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/places")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Место в ряду 1 с номером 1 уже существует"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(20)
    void create_entityNotFoundException_session() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        PlaceRequest request = new PlaceRequest(
                105L,
                2,
                8,
                BigDecimal.valueOf(100.00),
                true
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/places")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Сеанс с идентификатором 105 не найден"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(21)
    void create_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        PlaceRequest request = new PlaceRequest(
                5L,
                2,
                8,
                BigDecimal.valueOf(100.00),
                true
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/places")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(22)
    void create_unauthorized() {
        PlaceRequest request = new PlaceRequest(
                4L,
                6,
                27,
                BigDecimal.valueOf(400.00),
                false
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .when()
                .post("/api/v1/places")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(23)
    void update_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        PlaceRequest request = new PlaceRequest(
                4L,
                6,
                29,
                BigDecimal.valueOf(400.00),
                true
        );
        RestAssured
                .given()
                .pathParam("id", 14)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.equalTo(14))
                .body("sessionId", Matchers.equalTo(4))
                .body("row", Matchers.equalTo(6))
                .body("number", Matchers.equalTo(29))
                .body("price", Matchers.equalTo("400.00"))
                .body("available", Matchers.equalTo(true));
    }

    @Test
    @Order(24)
    void update_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        PlaceRequest request = new PlaceRequest(
                null,
                null,
                null,
                null,
                null
        );
        RestAssured
                .given()
                .pathParam("id", 14)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.sessionId", Matchers.equalTo("Идентификатор сеанса не может быть пустым"))
                .body("fields.row", Matchers.equalTo("Номер ряда в зале не может быть пустым"))
                .body("fields.number", Matchers.equalTo("Номер места в зале не может быть пустым"))
                .body("fields.price", Matchers.equalTo("Цена за место не может быть пустой"))
                .body("fields.available", Matchers.equalTo("Доступность места не может быть пустым"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(25)
    void update_entityNotFoundException_session() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        PlaceRequest request = new PlaceRequest(
                105L,
                2,
                8,
                BigDecimal.valueOf(100.00),
                true
        );
        RestAssured
                .given()
                .pathParam("id", 14)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Сеанс с идентификатором 105 не найден"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(26)
    void update_entityAlreadyExistsException_existsBySessionIdAndRowAndNumber() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        PlaceRequest request = new PlaceRequest(
                6L,
                2,
                6,
                BigDecimal.valueOf(550.00),
                false
        );
        RestAssured
                .given()
                .pathParam("id", 51)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Место в ряду 2 с номером 6 уже существует"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(27)
    void update_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        PlaceRequest request = new PlaceRequest(
                3L,
                3,
                11,
                BigDecimal.valueOf(115.00),
                true
        );
        RestAssured
                .given()
                .pathParam("id", 51)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(28)
    void update_unauthorized() {
        PlaceRequest request = new PlaceRequest(
                5L,
                3,
                11,
                BigDecimal.valueOf(115.00),
                true
        );
        RestAssured
                .given()
                .pathParam("id", 51)
                .body(request)
                .contentType("application/json")
                .when()
                .put("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(29)
    void updateAvailabilityAtPlaces_ok() {
        List<Place> places = placeRepository.findAllByIds(Set.of(101L, 102L, 103L));
        Assertions.assertTrue(
                places.stream().allMatch(place -> place.getAvailable().equals(true))
        );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .queryParam("sessionId", 7)
                .queryParam("ids", Set.of(101, 102, 103))
                .queryParam("available", false)
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/places/ids/update/available-places")
                .then()
                .log().all()
                .statusCode(200);

        places = placeRepository.findAllByIds(Set.of(101L, 102L, 103L));
        Assertions.assertTrue(
                places.stream().allMatch(place -> place.getAvailable().equals(false))
        );
    }

    @Test
    @Order(30)
    void updateAvailabilityAtPlaces_unauthorized() {
        RestAssured
                .given()
                .queryParam("sessionId", 7)
                .queryParam("ids", Set.of(111, 112, 113))
                .queryParam("available", false)
                .when()
                .patch("/api/v1/places/ids/update/available-places")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(31)
    void deleteById_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .pathParam("id", 112)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(32)
    void deleteById_entityNotFoundException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .pathParam("id", 11999)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Место с идентификатором 11999 не найдено"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(33)
    void deleteById_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                .pathParam("id", 2)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(34)
    void deleteById_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", 2)
                .when()
                .delete("/api/v1/places/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }
}
