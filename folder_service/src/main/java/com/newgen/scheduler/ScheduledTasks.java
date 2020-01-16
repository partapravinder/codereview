package com.newgen.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.newgen.controller.CronJobController;

@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	CronJobController cronJobController;

	@Scheduled(initialDelayString = "${cron.job3.initial.delay}", fixedDelayString = "${cron.job3.fixed.delay}")
	public void cronJob3() {
		/*Cron Job to handle copy/delete folder*/
		logger.debug("Cron Job 1 started executing at : " + new Date());
		try {
			cronJobController.handleAsyncFolderOperations();
		} catch (Exception ex) {
			logger.debug(ex.getMessage(), ex);
		}
		logger.debug("Cron Job 1 finished executing at : " + new Date());
	}

	@Scheduled(initialDelayString = "${cron.job4.initial.delay}", fixedDelayString = "${cron.job4.fixed.delay}")
	public void cronJob4() {
		// Cron Job to delete the copyFolder 5 minutes after the status was
		// changed to committed
		logger.debug("Cron Job 2 started executing at : " + new Date());
		try {
			cronJobController.deleteCompletedFailedAsyncFolderOperations();
		} catch (Exception ex) {
			logger.debug(ex.getMessage(), ex);
		}
		logger.debug("Cron Job 2 finished executing at : " + new Date());
	}
	
//	@Scheduled(initialDelayString = "${cron.job5.initial.delay}", fixedDelayString = "${cron.job5.fixed.delay}")
//	public void cronJob5() {
//	 /*Cron Job to handle copy/delete folder*/
//	 logger.debug("Cron Job 5 started executing at : " + new Date());
//	 try {
//	  cronJobController.uploadLogFile();
//	 } catch (Exception ex) {
//	  logger.debug(ex.getMessage(), ex);
//	 }
//	 logger.debug("Cron Job 5 finished executing at : " + new Date());
//	}

	
}
