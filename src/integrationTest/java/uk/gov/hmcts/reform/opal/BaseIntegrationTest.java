package uk.gov.hmcts.reform.opal;

import org.flywaydb.core.Flyway;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.hmcts.reform.opal.scheduler.config.CronJobConfiguration;
import uk.gov.hmcts.reform.opal.scheduler.config.QuartzConfiguration;
import uk.gov.hmcts.reform.opal.service.JobService;

import javax.sql.DataSource;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"integration"})
@SuppressWarnings("HideUtilityClassConstructor")
public class BaseIntegrationTest {

    @MockBean
    CronJobConfiguration cronJobConfiguration;
    @MockBean
    QuartzConfiguration quartzConfiguration;
    @MockBean
    JobService jobService;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        PostgreSQLContainer<?> postgresContainer = SingletonPostgreSQLContainer.getInstance();
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Configuration
    static class TestConfig {

        @Bean
        Flyway flyway(DataSource dataSource) {
            return Flyway.configure().dataSource(dataSource)
                .cleanDisabled(false)
                .locations("classpath:db/migration/allEnvs")
                .load();
        }
    }
}
