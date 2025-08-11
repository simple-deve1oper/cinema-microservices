package dev.dictionary.country.controller;

import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

@ActiveProfiles("test")
public class CountryControllerTest extends AbstractControllerTest {
    @Test
    void getAll_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    void getAll_unauthorized() {
        RestAssured
                .given()
                .when()
                    .get("/api/v1/dictionary/countries")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    void getByCode_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
                .given()
                    .queryParam("code", "643")
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries/search")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.equalTo(3))
                    .body("code", Matchers.equalTo("643"))
                    .body("name", Matchers.equalTo("Россия"));
    }

    @Test
    void getByCode_notFound() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        RestAssured
            .given()
                .queryParam("code", "001")
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/v1/dictionary/countries/search")
            .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Страна с кодом 001 не найдена"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    void getByCode_unauthorized() {
        RestAssured
                .given()
                    .queryParam("code", "056")
                .when()
                    .get("/api/v1/dictionary/countries/search")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    void getAllByCodes_all() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<String> values = Set.of("380", "056", "152", "643");
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries/search/codes")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.equalTo(4))
                    .body("code", Matchers.hasItem("380"))
                    .body("code", Matchers.hasItem("056"))
                    .body("code", Matchers.hasItem("152"))
                    .body("code", Matchers.hasItem("643"));
    }

    @Test
    void getAllByCodes_some() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<String> values = Set.of("380", "001", "152", "002");
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries/search/codes")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.equalTo(2))
                    .body("code", Matchers.hasItem("380"))
                    .body("code", Matchers.hasItem("152"));
    }

    @Test
    void getAllByCodes_empty() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<String> values = Set.of("001", "002", "003", "004");
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries/search/codes")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.equalTo(0));
    }

    @Test
    void getAllByCodes_unauthorized() {
        Set<String> values = Set.of("380", "056", "152", "643");
        RestAssured
                .given()
                    .queryParam("values", values)
                .when()
                    .get("/api/v1/dictionary/countries/code")
                .then()
                    .log().all()
                    .statusCode(401);
    }

    @Test
    void getNonExistentCodes_all() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<String> values = Set.of("001", "002", "003", "004");
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries/search/not-exists/codes")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.hasSize(4))
                    .body("$.", Matchers.hasItem("001"))
                    .body("$.", Matchers.hasItem("002"))
                    .body("$.", Matchers.hasItem("003"))
                    .body("$.", Matchers.hasItem("004"));
    }

    @Test
    void getNonExistentCodes_some() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<String> values = Set.of("001", "056", "003", "152");
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries/search/not-exists/codes")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$", Matchers.hasSize(2))
                    .body("$.", Matchers.hasItem("001"))
                    .body("$.", Matchers.hasItem("003"));
    }

    @Test
    void getNonExistentCodes_empty() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.CLIENT_CREDENTIALS, clientId, clientSecret);

        Set<String> values = Set.of("380", "056", "152", "643");
        RestAssured
                .given()
                    .queryParam("values", values)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/dictionary/countries/search/not-exists/codes")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.equalTo(0));
    }

    @Test
    void getNonExistentCodes_unauthorized() {
        Set<String> values = Set.of("001", "002", "003", "004");
        RestAssured
                .given()
                    .queryParam("values", values)
                .when()
                    .get("/api/v1/dictionary/countries/not-exists/code")
                .then()
                    .log().all()
                    .statusCode(401);
    }
}
