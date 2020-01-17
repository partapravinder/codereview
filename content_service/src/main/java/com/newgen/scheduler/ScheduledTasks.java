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

	@Scheduled(initialDelayString = "${cron.job1.initial.delay}", fixedDelayString = "${cron.job1.fixed.delay}")
	public void cronJob1() {
		logger.debug("Cron Job 1 started executing at : " + new Date());
		
		try {
			cronJobController.handleDeleteContent();
			cronJobController.handleDeleteContentLocation();
		} catch (Exception ex) {
			logger.debug(ex.getMessage(), ex);
		}
		logger.debug("Cron Job 1 finished executing at : " + new Date());
	}

	@Scheduled(initialDelayString = "${cron.job2.initial.delay}", fixedDelayString = "${cron.job2.fixed.delay}")
	public void cronJob2() {
		logger.debug("Cron Job 2 started executing at : " + new Date());
		try {
			cronJobController.handleDanglingContentLocation();
		} catch (Exception ex) {
			logger.debug(ex.getMessage(), ex);
		}
		logger.debug("Cron Job 2 finished executing at : " + new Date());
	}
	
	

	}
