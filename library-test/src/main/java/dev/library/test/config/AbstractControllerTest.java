package dev.library.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import dev.library.test.dto.constant.GrantType;
import dev.library.test.dto.constant.TestContainerConstants;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.web.client.RestClient;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public abstract class AbstractControllerTest {
    @Value("${keycloak.client-id}")
    protected String clientId;
    @Value("${keycloak.client-secret}")
    protected String clientSecret;

    protected final ObjectMapper mapper = new ObjectMapper();

    public AbstractControllerTest() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Container
    protected static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER_ENTITY =
            new PostgreSQLContainer<>(TestContainerConstants.POSTGRES_IMAGE)
                    .withDatabaseName(TestContainerConstants.ENTITY_DATABASE)
                    .withUsername(TestContainerConstants.POSTGRES_USERNAME)
                    .withPassword(TestContainerConstants.POSTGRES_PASSWORD);

    @ServiceConnection
    protected static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER_KEYCLOAK =
            new PostgreSQLContainer<>(TestContainerConstants.POSTGRES_IMAGE)
                    .withDatabaseName(TestContainerConstants.KEYCLOAK_DATABASE)
                    .withUsername(TestContainerConstants.POSTGRES_USERNAME)
                    .withPassword(TestContainerConstants.POSTGRES_PASSWORD);
    @Container
    protected static KeycloakContainer KEYCLOAK_CONTAINER =
            new KeycloakContainer(TestContainerConstants.KEYCLOAK_IMAGE)
                    .withRealmImportFile(TestContainerConstants.REALM_IMPORT_FILE)
                    .withAdminUsername("admin")
                    .withAdminPassword("admin");

    @SuppressWarnings("unused")
    @DynamicPropertySource
    static void jwtValidationProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER_ENTITY::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER_ENTITY::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER_ENTITY::getPassword);

        registry.add(
                "spring.security.oauth2.client.registration.keycloak.provider",
                () -> "keycloak"
        );
        registry.add(
                "spring.security.oauth2.client.registration.keycloak.client-id",
                () -> "gateway-client"
        );
        registry.add(
                "spring.security.oauth2.client.registration.keycloak.client-secret",
                () -> "lax66b9pOJMmfBEG4e9A8AyKrzRISOSY"
        );
        registry.add(
                "spring.security.oauth2.client.registration.keycloak.authorization-grant-type",
                GrantType.CLIENT_CREDENTIALS::getName
        );
        registry.add(
                "spring.security.oauth2.client.registration.keycloak.scope[0]",
                () -> "metrics"
        );
        registry.add(
                "spring.security.oauth2.client.registration.keycloak.scope[1]",
                () -> "eureka"
        );

        registry.add(
                "spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () ->"%s/realms/%s".formatted(KEYCLOAK_CONTAINER.getAuthServerUrl(), TestContainerConstants.REALM_NAME)
        );
        registry.add(
                "spring.security.oauth2.client.provider.keycloak.token-uri",
                () -> "%s/realms/%s/protocol/openid-connect/token".formatted(KEYCLOAK_CONTAINER.getAuthServerUrl(), TestContainerConstants.REALM_NAME)
        );

        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> "%s/realms/%s".formatted(KEYCLOAK_CONTAINER.getAuthServerUrl(), TestContainerConstants.REALM_NAME)
        );
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> "%s/realms/%s/protocol/openid-connect/certs".formatted(KEYCLOAK_CONTAINER.getAuthServerUrl(), TestContainerConstants.REALM_NAME)
        );
    }

    @LocalServerPort
    protected Integer port;
    protected RestClient restClient;

    @BeforeEach
    protected void init() {
        final var baseUrl = KEYCLOAK_CONTAINER.getAuthServerUrl() + "/realms/" + TestContainerConstants.REALM_NAME;
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
}
