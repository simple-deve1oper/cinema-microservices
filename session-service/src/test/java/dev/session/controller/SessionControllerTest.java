package dev.session.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.library.core.util.DateUtil;
import dev.library.domain.session.dto.SessionRequest;
import dev.library.domain.session.dto.SessionSearchRequest;
import dev.library.domain.session.dto.constant.MovieFormat;
import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableWireMock({
        @ConfigureWireMock(name = "movie-service", port = 8161)
})
public class SessionControllerTest extends AbstractControllerTest {
    @InjectWireMock("movie-service")
    WireMockServer mockMovieService;

    @Test
    @Order(1)
    void getAll_ok() {
        RestAssured
                .given()
                .when()
                .get("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    @Order(2)
    void getAll_some() {
        SessionSearchRequest request = new SessionSearchRequest(
                2L,
                LocalDate.now().plusDays(2)
        );
        Map<String, String> params = new HashMap<>();
        params.put("movieId", request.getMovieId().toString());
        params.put("date", request.getDate().toString());

        RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(params)
                .when()
                .get("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.equalTo(1))
                .body("[0].id", Matchers.equalTo(5))
                .body("[0].movieId", Matchers.equalTo(2))
                .body("[0].movieFormat", Matchers.equalTo("3D"))
                .body("[0].hall", Matchers.equalTo(2))
                .body("[0].dateTime", Matchers.notNullValue())
                .body("[0].available", Matchers.equalTo(true));
    }

    @Test
    @Order(3)
    void getById_ok() {
        RestAssured
                .given()
                .pathParam("id", 1)
                .when()
                .get("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.equalTo(1))
                .body("movieId", Matchers.equalTo(1))
                .body("movieFormat", Matchers.equalTo("2D"))
                .body("hall", Matchers.equalTo(2))
                .body("dateTime", Matchers.notNullValue())
                .body("available", Matchers.equalTo(false));
    }

    @Test
    @Order(4)
    void getById_entityNotFoundException() {
        RestAssured
                .given()
                .pathParam("id", 11999)
                .when()
                .get("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Сеанс с идентификатором 11999 не найден"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(5)
    void create_ok() {
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/exists/[^/]+"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
                1L,
                MovieFormat.TWO_D,
                4,
                OffsetDateTime.now().plusDays(1),
                true
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", Matchers.equalTo(9))
                .body("movieId", Matchers.equalTo(1))
                .body("movieFormat", Matchers.equalTo("2D"))
                .body("hall", Matchers.equalTo(4))
                .body("dateTime", Matchers.notNullValue())
                .body("available", Matchers.equalTo(true));

    }

    @Test
    @Order(6)
    void create_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
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
                .post("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.movieId", Matchers.equalTo("Идентификатор фильма не может быть пустым"))
                .body("fields.movieFormat", Matchers.equalTo("Формат фильма не может быть пустым"))
                .body("fields.hall", Matchers.equalTo("Номер зала не может быть пустым"))
                .body("fields.dateTime", Matchers.equalTo("Дата и время сеанса не может быть пустым"))
                .body("fields.available", Matchers.equalTo("Доступность сеанса не может быть пустой"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(7)
    void create_entityNotFoundException_notExistsMovieById() {
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/exists/[^/]+"))
                                .willReturn(
                                        WireMock.ok("false").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
                2887L,
                MovieFormat.TWO_D,
                1,
                OffsetDateTime.now().plusDays(12),
                false
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Фильм с идентификатором 2887 не найден"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(8)
    void create_entityAlreadyExistsException_existsByHallAndDateTime() {
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/exists/[^/]+"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        LocalDate date = LocalDate.of(2025, 6, 20);
        LocalTime time = LocalTime.of(20, 40);
        ZoneOffset offset = ZoneOffset.UTC;
        OffsetDateTime dateTime = OffsetDateTime.of(date, time, offset);
        SessionRequest request = new SessionRequest(
                2L,
                MovieFormat.THREE_D,
                3,
                dateTime,
                true
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Сеанс в зале 3 на время %s уже существует".formatted(DateUtil.formatDate(dateTime))))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(9)
    void create_movieService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
                1L,
                MovieFormat.TWO_D,
                2,
                OffsetDateTime.now().plusDays(5),
                false
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис фильмов временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(10)
    void create_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        SessionRequest request = new SessionRequest(
                1L,
                MovieFormat.TWO_D,
                2,
                OffsetDateTime.now().plusDays(5),
                false
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(11)
    void create_unauthorized() {
        SessionRequest request = new SessionRequest(
                1L,
                MovieFormat.TWO_D,
                2,
                OffsetDateTime.now().plusDays(5),
                false
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .when()
                .post("/api/v1/sessions")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(12)
    void update_ok() {
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/exists/[^/]+"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
                1L,
                MovieFormat.TWO_D,
                2,
                OffsetDateTime.now().plusDays(6),
                true
        );
        RestAssured
                .given()
                .pathParam("id", 6)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.equalTo(6))
                .body("movieId", Matchers.equalTo(1))
                .body("movieFormat", Matchers.equalTo("2D"))
                .body("hall", Matchers.equalTo(2))
                .body("dateTime", Matchers.notNullValue())
                .body("available", Matchers.equalTo(true));
    }

    @Test
    @Order(13)
    void update_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
                null,
                null,
                null,
                null,
                null
        );
        RestAssured
                .given()
                .pathParam("id", 5)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.movieId", Matchers.equalTo("Идентификатор фильма не может быть пустым"))
                .body("fields.movieFormat", Matchers.equalTo("Формат фильма не может быть пустым"))
                .body("fields.hall", Matchers.equalTo("Номер зала не может быть пустым"))
                .body("fields.dateTime", Matchers.equalTo("Дата и время сеанса не может быть пустым"))
                .body("fields.available", Matchers.equalTo("Доступность сеанса не может быть пустой"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(14)
    void update_entityNotFoundException_notExistsMovieById() {
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/exists/[^/]+"))
                                .willReturn(
                                        WireMock.ok("false").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
                2887L,
                MovieFormat.TWO_D,
                1,
                OffsetDateTime.now().plusDays(12),
                false
        );
        RestAssured
                .given()
                .pathParam("id", 3)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Фильм с идентификатором 2887 не найден"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(15)
    void update_entityAlreadyExistsException_existsByHallAndDateTime() {
        mockMovieService
                .stubFor(
                        WireMock.get(WireMock.urlPathMatching("/api/v1/movies/exists/[^/]+"))
                                .willReturn(
                                        WireMock.ok("true").withHeader("Content-Type", "application/json")
                                )
                );

        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        LocalDate date = LocalDate.of(2025, 6, 20);
        LocalTime time = LocalTime.of(20, 40);
        ZoneOffset offset = ZoneOffset.UTC;
        OffsetDateTime dateTime = OffsetDateTime.of(date, time, offset);
        SessionRequest request = new SessionRequest(
                2L,
                MovieFormat.THREE_D,
                3,
                dateTime,
                true
        );
        RestAssured
                .given()
                .pathParam("id", 4)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Сеанс в зале 3 на время %s уже существует".formatted(DateUtil.formatDate(dateTime))))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(16)
    void update_movieService_unavailable() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        SessionRequest request = new SessionRequest(
                2L,
                MovieFormat.THREE_D,
                4,
                OffsetDateTime.now().plusDays(100),
                false
        );
        RestAssured
                .given()
                .pathParam("id", 1)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("code", Matchers.equalTo(500))
                .body("message", Matchers.equalTo("Сервис фильмов временно недоступен, повторите попытку позже!"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(17)
    void update_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        SessionRequest request = new SessionRequest(
                2L,
                MovieFormat.THREE_D,
                3,
                OffsetDateTime.now().plusDays(10),
                true
        );
        RestAssured
                .given()
                .pathParam("id", 2)
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(18)
    void update_unauthorized() {
        SessionRequest request = new SessionRequest(
                2L,
                MovieFormat.THREE_D,
                3,
                OffsetDateTime.now().plusDays(10),
                true
        );
        RestAssured
                .given()
                .pathParam("id", 2)
                .body(request)
                .contentType("application/json")
                .when()
                .put("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(19)
    void deleteById_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .pathParam("id", 1)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(19)
    void deleteById_entityNotFoundException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .pathParam("id", 11999)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Сеанс с идентификатором 11999 не найден"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(19)
    void deleteById_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                .pathParam("id", 4)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(19)
    void deleteById_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", 2)
                .when()
                .delete("/api/v1/sessions/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }
}
