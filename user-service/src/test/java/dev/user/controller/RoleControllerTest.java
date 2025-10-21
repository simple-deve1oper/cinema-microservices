package dev.user.controller;

import dev.user.config.AbstractControllerTest;
import dev.user.config.AuthorizationUtils;
import dev.user.config.GrantType;
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

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleControllerTest extends AbstractControllerTest {
    private static final Logger log = LoggerFactory.getLogger(RoleControllerTest.class);

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
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/roles")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", Matchers.greaterThan(0));
    }

    @Test
    @Order(2)
    void getAll_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/roles")
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
                .get("/api/v1/roles")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @Order(4)
    void updateRoleToUser_ok() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "admin5876", "1234");

        RestAssured
                .given()
                .pathParam("id", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .queryParam("currentRole", "manager")
                .queryParam("newRole", "client")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/roles/user/{id}")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(5)
    void updateRoleToUser_forbidden() {
        String token = AuthorizationUtils.getToken(restClient, GrantType.PASSWORD, clientId, clientSecret, "ivan5436", "1234");

        RestAssured
                .given()
                .pathParam("id", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .queryParam("currentRole", "manager")
                .queryParam("newRole", "client")
                .header("Authorization", "Bearer " + token)
                .when()
                .put("/api/v1/roles/user/{id}")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @Order(6)
    void updateRoleToUser_unauthorized() {
        RestAssured
                .given()
                .pathParam("id", "14b8135e-4a62-4104-ac6a-26eefaeeef17")
                .queryParam("currentRole", "manager")
                .queryParam("newRole", "client")
                .when()
                .put("/api/v1/roles/user/{id}")
                .then()
                .log().all()
                .statusCode(401);
    }
}
