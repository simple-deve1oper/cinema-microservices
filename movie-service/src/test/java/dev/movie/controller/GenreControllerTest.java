package dev.movie.controller;

import dev.library.test.config.AbstractControllerTest;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.util.AuthorizationUtils;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class GenreControllerTest extends AbstractControllerTest {
    @Test
    void getAll_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/genres")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    void getAll_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get("/api/v1/genres")
                .then()
                    .log().all()
                    .statusCode(403);
    }

    @Test
    void getAll_unauthorized() {
        RestAssured
                .given()
                .when()
                    .get("/api/v1/genres")
                .then()
                    .log().all()
                    .statusCode(401);
    }
}
