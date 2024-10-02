package uk.gov.hmcts.reform.opal.scheduler.job.inbound;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.scheduler.model.CronJob;
import uk.gov.hmcts.reform.opal.service.DwpBailiffsService;

@Component
@Getter
@Slf4j
@DisallowConcurrentExecution
public class DwpBailiffsJob implements CronJob {

    @Value("${opal.schedule.dwp-bailiffs-job.cron}")
    private String cronExpression;

    @Autowired
    private DwpBailiffsService dwpBailiffsService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            log.info("Job ** {} ** starting @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

            dwpBailiffsService.process();

            log.info(
                "Job ** {} ** completed.  Next job scheduled @ {}",
                context.getJobDetail().getKey().getName(),
                context.getNextFireTime()
            );
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }

}
