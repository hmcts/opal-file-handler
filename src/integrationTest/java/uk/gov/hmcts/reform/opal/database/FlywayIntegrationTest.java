package uk.gov.hmcts.reform.opal.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.opal.BaseIntegrationTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration")
public class FlywayIntegrationTest extends BaseIntegrationTest {

    @Autowired
    Flyway flyway;

    @Test
    public void testFlywayMigrationsDev() {

        List<MigrationInfo> migrations = Arrays.asList(flyway.info().all());
        assertTrue(migrations.stream().anyMatch(m -> m.getScript().contains("_001_")));
    }
}


