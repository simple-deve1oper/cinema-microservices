package dev.library.test.config;

import dev.library.test.dto.constant.TestContainerConstants;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
public abstract class AbstractRepositoryTest {
    @Container
    protected static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER_ENTITY_REPOSITORY =
            new PostgreSQLContainer<>(TestContainerConstants.POSTGRES_IMAGE)
                    .withDatabaseName(TestContainerConstants.ENTITY_DATABASE)
                    .withUsername(TestContainerConstants.POSTGRES_USERNAME)
                    .withPassword(TestContainerConstants.POSTGRES_PASSWORD);

    @DynamicPropertySource
    protected static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER_ENTITY_REPOSITORY::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER_ENTITY_REPOSITORY::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER_ENTITY_REPOSITORY::getPassword);
    }
}
