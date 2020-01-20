/*
 * package com.newgen.scheduler;
 * 
 * import java.util.Date;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Component;
 * 
 * import com.newgen.controller.CronJobController;
 * 
 * @Component public class ScheduledTasks {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(ScheduledTasks.class);
 * 
 * @Autowired CronJobController cronJobController;
 * 
 * @Scheduled(initialDelayString = "${cron.job1.initial.delay}",
 * fixedDelayString = "${cron.job1.fixed.delay}") public void cronJob1() { Cron
 * Job to handle upload and delete of the storage Process queued in a redis
 * queue with id, which is polled and then queried from storage process
 * collection in mongodb and then the corresponding action is performed.
 * logger.debug("Cron Job 1 started executing at : " + new Date()); try {
 * cronJobController.handleStorageProcess(); } catch (Exception ex) {
 * logger.debug(ex.getMessage(), ex); }
 * logger.debug("Cron Job 1 finished executing at : " + new Date()); }
 * 
 * @Scheduled(initialDelayString = "${cron.job2.initial.delay}",
 * fixedDelayString = "${cron.job2.fixed.delay}") public void cronJob2() { Cron
 * Job to delete the storage process 5 minutes after the status was changed to
 * committed logger.debug("Cron Job 2 started executing at : " + new Date());
 * try { cronJobController.deleteAcknowledFailedStorageProcess(); } catch
 * (Exception ex) { logger.debug(ex.getMessage(), ex); }
 * logger.debug("Cron Job 2 finished executing at : " + new Date()); }
 * 
 * }
 */