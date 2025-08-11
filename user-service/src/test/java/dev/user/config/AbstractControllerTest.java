package dev.user.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.Objects;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public abstract class AbstractControllerTest {
    @Value("${keycloak.client-id}")
    protected String clientId;
    @Value("${keycloak.client-secret}")
    protected String clientSecret;

    static KeycloakAdminHelper KEYCLOAK_ADMIN_HELPER;

    protected static Network NETWORK = Network.newNetwork();
    protected static GenericContainer<?> MAILDEV;
    protected static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER_KEYCLOAK;
    protected static KeycloakContainer KEYCLOAK_CONTAINER;

    @BeforeAll
    static void setup() {
        MAILDEV = new GenericContainer<>("maildev/maildev:latest")
                .withExposedPorts(1080, 1025)
                .withNetwork(NETWORK)
                .withNetworkAliases("maildev");
        MAILDEV.start();

        String smtpHost = MAILDEV.getContainerName().replace("/", "");
        POSTGRE_SQL_CONTAINER_KEYCLOAK =
                new PostgreSQLContainer<>(TestContainerConstants.POSTGRES_IMAGE)
                        .withNetwork(NETWORK)
                        .withNetworkAliases("postgres")
                        .withDatabaseName(TestContainerConstants.KEYCLOAK_DATABASE)
                        .withUsername(TestContainerConstants.POSTGRES_USERNAME)
                        .withPassword(TestContainerConstants.POSTGRES_PASSWORD);
        POSTGRE_SQL_CONTAINER_KEYCLOAK.start();
        KEYCLOAK_CONTAINER =
                new KeycloakContainer(TestContainerConstants.KEYCLOAK_IMAGE)
                        .withRealmImportFile(TestContainerConstants.REALM_IMPORT_FILE)
                        .withAdminUsername("admin")
                        .withAdminPassword("admin")
                        .withNetwork(NETWORK)
                        .withNetworkAliases("keycloak");
        KEYCLOAK_CONTAINER.start();

        KEYCLOAK_ADMIN_HELPER = new KeycloakAdminHelper(KEYCLOAK_CONTAINER.getAuthServerUrl());
        RealmRepresentation realmRepresentation = KEYCLOAK_ADMIN_HELPER.getSettingsRealm();
        Map<String, String> smtpServer = Objects.requireNonNull(realmRepresentation).getSmtpServer();
        smtpServer.put("host", smtpHost);
        realmRepresentation.setSmtpServer(smtpServer);
        KEYCLOAK_ADMIN_HELPER.editSettingsRealm(realmRepresentation);
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("keycloak.realm", () -> "cinema");
        registry.add("keycloak.admin-client-id", () -> "admin-cli");
        registry.add("keycloak.admin-client-secret", () -> "wN3n1aFc0lrKrCKgfTnvEFsOO4TmYrwb");
        registry.add("keycloak.url.auth", () -> "%s".formatted(KEYCLOAK_CONTAINER.getAuthServerUrl()));

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
                () -> "client_credentials"
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
        final var baseUrl = KEYCLOAK_CONTAINER.getAuthServerUrl() + "/realms/cinema";
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
}
