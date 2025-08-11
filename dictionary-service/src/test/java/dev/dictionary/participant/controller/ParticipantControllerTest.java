package dev.dictionary.participant.controller;

import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

@ActiveProfiles("test")
public class ParticipantControllerTest extends AbstractControllerTest {
    @Test
    void getAll_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    void getAll_unauthorized() {
        RestAssured
                .given()
                    .contentType("application/json")
                .when()
                    .get("/api/v1/dictionary/participants")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    void getById_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .pathParam("id", 5)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(5))
                    .body("lastName", Matchers.equalTo("Мажори"))
                    .body("firstName", Matchers.equalTo("Стив"))
                    .body("middleName", Matchers.nullValue());
    }

    @Test
    void getById_notFound() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .pathParam("id", 1025)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/{id}")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", Matchers.equalTo(404))
                    .body("message", Matchers.equalTo("Участник с идентификатором 1025 не найден"))
                    .body("dateTime", Matchers.notNullValue());
    }

    @Test
    void getById_unauthorized() {
        RestAssured
                .given()
                    .pathParam("id", 6)
                .when()
                    .get("/api/v1/dictionary/participants/{id}")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    void getAllByIds_all() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(1L, 2L, 8L, 10L);
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/search/ids")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.equalTo(4))
                    .body("id", Matchers.hasItem(1))
                    .body("id", Matchers.hasItem(2))
                    .body("id", Matchers.hasItem(8))
                    .body("id", Matchers.hasItem(10));
    }

    @Test
    void getAllByIds_some() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(1L, 2003L, 8L, 1007L);
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/search/ids")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.equalTo(2))
                    .body("id", Matchers.hasItem(1))
                    .body("id", Matchers.hasItem(8));
    }

    @Test
    void getAllByIds_empty() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(1001L, 967L, 567L, 1025L);
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/search/ids")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.equalTo(0));
    }

    @Test
    void getAllByIds_unauthorized() {
        Set<Long> values = Set.of(1L, 2L, 8L, 10L);
        RestAssured
                .given()
                    .queryParam("values", values)
                .when()
                    .get("/api/v1/dictionary/participants/ids")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    void getNonExistentIds_all() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(1001L, 967L, 567L, 1025L);
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/search/not-exists/ids")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.hasSize(4))
                    .body("$.", Matchers.hasItem(1001))
                    .body("$.", Matchers.hasItem(967))
                    .body("$.", Matchers.hasItem(567))
                    .body("$.", Matchers.hasItem(1025));
    }

    @Test
    void getNonExistentIds_some() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(1L, 967L, 567L, 8L);
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/search/not-exists/ids")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.hasSize(2))
                    .body("$.", Matchers.hasItem(967))
                    .body("$.", Matchers.hasItem(567));
    }

    @Test
    void getNonExistentIds_empty() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<Long> values = Set.of(1L, 2L, 3L, 4L);
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/participants/search/not-exists/ids")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.hasSize(0));
    }

    @Test
    void getNonExistentIds_unauthorized() {
        Set<Long> values = Set.of(1001L, 967L, 567L, 1025L);
        RestAssured
                .given()
                    .queryParam("values", values)
                .when()
                    .get("/api/v1/dictionary/participants/not-exists/ids")
                .then()
                    .log().all()
                    .statusCode(401);
    }
}
