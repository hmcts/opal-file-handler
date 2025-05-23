package uk.gov.hmcts.reform.opal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import uk.gov.hmcts.reform.opal.scheduler.exception.JobException;
import uk.gov.hmcts.reform.opal.scheduler.model.JobData;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

class JobServiceTest {

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private JobService jobsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldScheduleNewJob() throws SchedulerException {
        //given
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);

        ZonedDateTime startDateTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC);

        //when
        JobKey jobKey = jobsService.scheduleJob(jobData, startDateTime);
        //then

        assertThat(jobKey).isEqualTo(new JobKey(jobId, group));
        JobDetail jobDetail = getJobDetails(jobData);
        Trigger trigger = getTrigger(startDateTime, jobData);
        verify(scheduler).scheduleJob(jobDetail, trigger);
    }

    @Test
    void shouldRescheduleOldJob() throws SchedulerException {
        //given
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);
        ZonedDateTime startDateTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC);

        //when

        //schedule new job
        JobKey jobKey = jobsService.scheduleJob(jobData, startDateTime);

        //reschedule job again
        ZonedDateTime newStartDateTime = LocalDate.now().plusDays(5).atStartOfDay(ZoneOffset.UTC);
        when(scheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class)))
            .thenReturn(Date.from(newStartDateTime.toInstant()));

        jobsService.rescheduleJob(jobData, newStartDateTime);

        //then

        //verify scheduling
        assertThat(jobKey).isEqualTo(new JobKey(jobId, group));
        verify(scheduler).scheduleJob(getJobDetails(jobData), getTrigger(startDateTime, jobData));

        //verify rescheduling
        verify(scheduler).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
    }

    @Test
    void shouldScheduleAnotherJobWhenReschedulingOldJobFails() throws SchedulerException {
        //given
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);

        ZonedDateTime startDateTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC);

        //when
        //schedule new job
        JobKey jobKey = jobsService.scheduleJob(jobData, startDateTime);

        //reschedule job again
        ZonedDateTime newStartDateTime = LocalDate.now().plusDays(5).atStartOfDay(ZoneOffset.UTC);
        when(scheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class)))
            .thenReturn(null);

        jobsService.rescheduleJob(jobData, newStartDateTime);

        //then
        //verify scheduling
        assertThat(jobKey).isEqualTo(new JobKey(jobId, group));
        verify(scheduler, atLeast(2)).scheduleJob(getJobDetails(jobData), getTrigger(startDateTime, jobData));
        //verify rescheduling
        verify(scheduler).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
    }

    @Test
    void shouldScheduleNewCronJob() throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        String cronExpression = "0 * * * * ?";
        JobKey jobKey = jobsService.scheduleJob(jobData, cronExpression);

        assertThat(jobKey).isEqualTo(new JobKey(jobId, group));

        JobDetail jobDetail = getJobDetails(jobData);
        Trigger trigger = getCronTrigger(cronExpression, jobData);
        verify(scheduler).scheduleJob(jobDetail, trigger);
    }

    @Test
    void shouldNotScheduleNewCronJobIfExpressionIsBlank() throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);

        String cronExpression = "";
        JobKey jobKey = jobsService.scheduleJob(jobData, cronExpression);

        assertThat(jobKey).isNull();
        verify(scheduler, never()).scheduleJob(any(), any());
    }

    @Test
    void shouldDeleteExistingCronJob() throws SchedulerException {
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);
        String cronExpression = "0 * * * * ?";
        JobKey key = getJobDetails(jobData).getKey();
        when(scheduler.checkExists(key)).thenReturn(true);

        JobKey jobKey = jobsService.scheduleJob(jobData, cronExpression);

        verify(scheduler).deleteJob(key);
    }

    @Test
    public void shouldClearDownOldJobs() throws SchedulerException {
        jobsService.clearJobs();

        verify(scheduler).clear();

    }

    @Test
    void errorScheduleNewJob() throws SchedulerException {
        //given
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);

        ZonedDateTime startDateTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC);

        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
            .thenThrow(SchedulerException.class);

        assertThrows(JobException.class, () -> {
            jobsService.scheduleJob(jobData, startDateTime);
        });

    }

    @Test
    void errorScheduleNewJobWithCron() throws SchedulerException, ParseException {
        //given
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);

        CronExpression cronExpression = new CronExpression("0 * * * * ?");

        when(scheduler.scheduleJob(any(JobDetail.class), any(CronTrigger.class)))
            .thenThrow(SchedulerException.class);

        assertThrows(JobException.class, () -> {
            jobsService.scheduleJob(jobData, cronExpression.toString());
        });

    }

    @Test
    void errorRescheduleJob() throws SchedulerException {
        //given
        String jobId = UUID.randomUUID().toString();
        String group = "Reminders";
        JobData jobData = getJobData(jobId, group);

        ZonedDateTime startDateTime = LocalDate.now().atStartOfDay(ZoneOffset.UTC);

        when(scheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class)))
            .thenThrow(SchedulerException.class);

        assertThrows(JobException.class, () -> {
            jobsService.rescheduleJob(jobData, startDateTime);
        });

    }

    private JobData getJobData(String jobId, String group) {
        Map<String, Object> data = new HashMap<>();
        data.put("fileId", "234324332432432");
        data.put("NoteReference", "REf123");
        data.put("user", "j.smith@example.com");

        return JobData.builder()
            .id(jobId)
            .group(group)
            .description("Mock job scheduler")
            .data(data)
            .jobClass(Job.class)
            .build();
    }

    private Trigger getTrigger(ZonedDateTime startDateTime, JobData jobData) {
        return newTrigger()
            .startAt(Date.from(startDateTime.toInstant()))
            .withIdentity(jobData.getId(), jobData.getGroup())
            .withSchedule(
                simpleSchedule()
                    .withMisfireHandlingInstructionNowWithExistingCount()
            )
            .build();
    }

    private Trigger getCronTrigger(String cronExpression, JobData jobData) {
        return newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .withIdentity(jobData.getId(), jobData.getGroup())
            .withDescription(jobData.getDescription())
            .build();
    }

    private JobDetail getJobDetails(JobData jobData) {
        return JobBuilder.newJob(jobData.getJobClass())
            .withIdentity(jobData.getId(), jobData.getGroup())
            .withDescription(jobData.getDescription())
            .usingJobData(new JobDataMap(jobData.getData()))
            .requestRecovery()
            .build();
    }

}
