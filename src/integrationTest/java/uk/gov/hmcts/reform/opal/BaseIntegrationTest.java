package uk.gov.hmcts.reform.opal;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.hmcts.reform.opal.scheduler.config.CronJobConfiguration;
import uk.gov.hmcts.reform.opal.scheduler.config.QuartzConfiguration;
import uk.gov.hmcts.reform.opal.service.JobService;

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

    @ServiceConnection
    @Container
    static PostgreSQLContainer databaseContainer = new PostgreSQLContainer<>("postgres:15-alpine");
}
