package dev.user.controller;

import dev.library.domain.user.dto.UserRegistrationRequest;
import dev.library.domain.user.dto.UserRequest;
import dev.user.config.AbstractControllerTest;
import dev.user.config.AuthorizationUtils;
import dev.user.config.GrantType;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest extends AbstractControllerTest {
    @Test
    @Order(1)
    void getAll_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/users")
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
                .get("/api/v1/users")
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
                .get("/api/v1/users")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(4)
    void getById_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.equalTo("3c59a7b2-4cff-49b6-a654-3145ecdab36b"))
                .body("username", Matchers.equalTo("dima1111"))
                .body("email", Matchers.equalTo("dima1111@example.com"))
                .body("emailVerified", Matchers.equalTo(true))
                .body("firstName", Matchers.equalTo("Dima"))
                .body("lastName", Matchers.equalTo("Lebovski"))
                .body("birthDate", Matchers.equalTo("2024-12-12"))
                .body("role.id", Matchers.equalTo("07a441c8-8512-49c9-b5dd-33a420334d4f"))
                .body("role.authority", Matchers.equalTo("client"))
                .body("active", Matchers.equalTo(true));
    }

    @Test
    @Order(5)
    void getById_accessForbiddenException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .pathParam("id", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(403)
                .body("code", Matchers.equalTo(403))
                .body("message", Matchers.equalTo("Доступ запрещён к просмотру чужой информации"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(6)
    void getById_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", "3ca5d554-4102-4fa5-bc54-c355502b1fe5")
                .when()
                .get("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(7)
    void create_ok() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "anton667",
                "1234567890",
                "anton667@example.com",
                "Anton",
                "Zelenov",
                LocalDate.of(2002, 3, 8)
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .when()
                .post("/api/v1/users")
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    @Order(8)
    void create_badRequestException_checkValidation() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                null,
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
                .when()
                .post("/api/v1/users")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.username", Matchers.equalTo("Username не может быть пустым"))
                .body("fields.password", Matchers.equalTo("Пароль не может быть пустым"))
                .body("fields.email", Matchers.equalTo("Электронная почта не может быть пустой"))
                .body("fields.firstName", Matchers.equalTo("Имя не может быть пустым"))
                .body("fields.lastName", Matchers.equalTo("Фамилия не может быть пустой"))
                .body("fields.birthDate", Matchers.equalTo("Дата рождения не может быть пустой"))
                .body("dateTime", Matchers.notNullValue());

        request = new UserRegistrationRequest(
                "",
                "",
                "test",
                "",
                "",
                LocalDate.of(2000, 1, 1)
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .when()
                .post("/api/v1/users")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.username", Matchers.equalTo("Username не может быть пустым"))
                .body("fields.password", Matchers.equalTo("Пароль должен содержать не менее 10 символов"))
                .body("fields.email", Matchers.equalTo("Электронная почта должна быть записана в формате 'test@example.com'"))
                .body("fields.firstName", Matchers.equalTo("Имя не может быть пустым"))
                .body("fields.lastName", Matchers.equalTo("Фамилия не может быть пустой"))
                .body("dateTime", Matchers.notNullValue());

        request = new UserRegistrationRequest(
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                "1234567890",
                "test@example.com",
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                LocalDate.of(2000, 1, 1)
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .when()
                .post("/api/v1/users")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.username", Matchers.equalTo("Username не может содержать более 255 символов"))
                .body("fields.firstName", Matchers.equalTo("Имя не может содержать более 255 символов"))
                .body("fields.lastName", Matchers.equalTo("Фамилия не может содержать более 255 символов"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(9)
    void create_entityAlreadyExistsException_username() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "dima1111",
                "1234567890",
                "dima1234@example.com",
                "Dima",
                "Test",
                LocalDate.of(2005, 5, 15)
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .when()
                .post("/api/v1/users")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Пользователь с username dima1111 уже существует"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(10)
    void create_entityAlreadyExistsException_email() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "dima1234",
                "1234567890",
                "dima1111@example.com",
                "Dima",
                "Test",
                LocalDate.of(2005, 5, 15)
        );
        RestAssured
                .given()
                .body(request)
                .contentType("application/json")
                .when()
                .post("/api/v1/users")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Пользователь с электронной почтой dima1111@example.com уже существует"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(11)
    void update_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        UserRequest request = new UserRequest(
                "test@mail.com",
                "Brian",
                "Williams",
                LocalDate.of(1970, 11, 21)
        );
        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", Matchers.equalTo("3c59a7b2-4cff-49b6-a654-3145ecdab36b"))
                .body("username", Matchers.equalTo("dima1111"))
                .body("email", Matchers.equalTo("test@mail.com"))
                .body("emailVerified", Matchers.equalTo(false))
                .body("firstName", Matchers.equalTo("Brian"))
                .body("lastName", Matchers.equalTo("Williams"))
                .body("birthDate", Matchers.equalTo("1970-11-21"))
                .body("role.id", Matchers.equalTo("07a441c8-8512-49c9-b5dd-33a420334d4f"))
                .body("role.authority", Matchers.equalTo("client"))
                .body("active", Matchers.equalTo(true));
    }

    @Test
    @Order(12)
    void update_accessForbiddenException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        UserRequest request = new UserRequest(
                "test@mail.com",
                "Test",
                "Test",
                LocalDate.of(1990, 10, 29)
        );
        RestAssured
                .given()
                .pathParam("id", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(403)
                .body("code", Matchers.equalTo(403))
                .body("message", Matchers.equalTo("Доступ запрещён к обновлению чужой информации"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(13)
    void update_badRequestException_checkValidation() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        UserRequest request = new UserRequest(
                null,
                null,
                null,
                null
        );
        RestAssured
                .given()
                .pathParam("id", "3ca5d554-4102-4fa5-bc54-c355502b1fe5")
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.email", Matchers.equalTo("Электронная почта не может быть пустой"))
                .body("fields.firstName", Matchers.equalTo("Имя не может быть пустым"))
                .body("fields.lastName", Matchers.equalTo("Фамилия не может быть пустой"))
                .body("fields.birthDate", Matchers.equalTo("Дата рождения не может быть пустой"))
                .body("dateTime", Matchers.notNullValue());

        request = new UserRequest(
                "test",
                "",
                "",
                LocalDate.of(1990, 10, 29)
        );
        RestAssured
                .given()
                .pathParam("id", "3ca5d554-4102-4fa5-bc54-c355502b1fe5")
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.email", Matchers.equalTo("Электронная почта должна быть записана в формате 'test@example.com'"))
                .body("fields.firstName", Matchers.equalTo("Имя не может быть пустым"))
                .body("fields.lastName", Matchers.equalTo("Фамилия не может быть пустой"))
                .body("dateTime", Matchers.notNullValue());

        request = new UserRequest(
                "test@example.com",
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                LocalDate.of(1990, 10, 29)
        );
        RestAssured
                .given()
                .pathParam("id", "3ca5d554-4102-4fa5-bc54-c355502b1fe5")
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message", Matchers.equalTo("Ошибка валидации"))
                .body("fields.firstName", Matchers.equalTo("Имя не может содержать более 255 символов"))
                .body("fields.lastName", Matchers.equalTo("Фамилия не может содержать более 255 символов"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(14)
    void update_entityAlreadyExistsException_email() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        UserRequest request = new UserRequest(
                "ivan5436@example.com",
                "Test",
                "Test",
                LocalDate.of(1990, 10, 29)
        );
        RestAssured
                .given()
                .pathParam("id", "3ca5d554-4102-4fa5-bc54-c355502b1fe5")
                .body(request)
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(409)
                .body("code", Matchers.equalTo(409))
                .body("message", Matchers.equalTo("Пользователь с электронной почтой ivan5436@example.com уже существует"))
                .body("dateTime", Matchers.notNullValue());
    }


    @Test
    @Order(15)
    void update_unauthorized() {
        UserRequest request = new UserRequest(
                "ivan5436@example.com",
                "Test",
                "Test",
                LocalDate.of(1990, 10, 29)
        );
        RestAssured
                .given()
                .pathParam("id", "3ca5d554-4102-4fa5-bc54-c355502b1fe5")
                .body(request)
                .contentType("application/json")
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(16)
    void deleteById_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .pathParam("id", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @Order(17)
    void deleteById_entityNotFoundException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        String id = UUID.randomUUID().toString();
        RestAssured
                .given()
                .pathParam("id", id)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Пользователь с идентификатором %s не найден".formatted(id)))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(18)
    void deleteById_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(19)
    void deleteById_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .when()
                .delete("/api/v1/users/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(20)
    void sendVerificationEmail_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "alex3865", "1234");

        RestAssured
                .given()
                .pathParam("id", "45de29ed-2c87-4c3e-9188-23d0611910ee")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/users/{id}/send-verify-email")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(21)
    void sendVerificationEmail_accessForbiddenException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "dima1111", "1234");

        RestAssured
                .given()
                .pathParam("id", "7116aae4-debe-473a-b492-4caee974dddd")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/users/{id}/send-verify-email")
                .then()
                .log().all()
                .statusCode(403)
                .body("code", Matchers.equalTo(403))
                .body("message", Matchers.equalTo("Доступ запрещён к вызову подтверждения email чужого профиля"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(23)
    void sendVerificationEmail_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", "7116aae4-debe-473a-b492-4caee974dddd")
                .when()
                .patch("/api/v1/users/{id}/send-verify-email")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(24)
    void forgotPassword_ok() {
        RestAssured
                .given()
                .queryParam("username", "dima1111")
                .when()
                .put("/api/v1/users/reset-password")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(25)
    void forgotPassword_entityNotFoundException() {
        RestAssured
                .given()
                .queryParam("username", "test1234")
                .when()
                .put("/api/v1/users/reset-password")
                .then()
                .log().all()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("message", Matchers.equalTo("Пользователь с username test1234 не найден"))
                .body("dateTime", Matchers.notNullValue());
    }

    @Test
    @Order(26)
    void updateActivity_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/users/{id}/activity")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(27)
    void updateActivity_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "alex3865", "1234");

        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/users/{id}/activity")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(28)
    void updateActivity_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .when()
                .patch("/api/v1/users/{id}/activity")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(29)
    void updatePassword_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "alex3865", "1234");

        RestAssured
                .given()
                .pathParam("id", "45de29ed-2c87-4c3e-9188-23d0611910ee")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}/update-password")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(30)
    void updatePassword_accessForbiddenException() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "alex3865", "1234");

        RestAssured
                .given()
                .pathParam("id", "3c59a7b2-4cff-49b6-a654-3145ecdab36b")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/users/{id}/update-password")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(31)
    void updatePassword_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", "7116aae4-debe-473a-b492-4caee974dddd")
                .when()
                .patch("/api/v1/users/{id}/update-password")
                .then()
                .log().all()
                .statusCode(401);
    }
}
