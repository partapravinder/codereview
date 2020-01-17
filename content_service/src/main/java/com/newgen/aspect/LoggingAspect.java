package com.newgen.aspect;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.newgen.logger.RequestCorrelation;

@Aspect
@Component
public class LoggingAspect {

	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	@Around("execution(* com.newgen.controller.ContentController.*(..)) || execution(* com.newgen.controller.CronJobController.*(..)) || execution(* com.newgen.service.ContentService.*(..) ) || execution(* com.newgen.service.ContentLocationService.*(..) ) || execution(* com.newgen.service.LockService.*(..) )")
//	@Around("@annotation(MethodLogger)")
	// @Around(value="execution(* *)) && (@annotation(Loggable)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

		StringBuilder inputArgs = new StringBuilder();
		long start = System.currentTimeMillis();

		Object[] signatureArgs = joinPoint.getArgs();
		for (Object signatureArg : signatureArgs) {
			if (signatureArg != null) {
				if (signatureArg instanceof HttpServletRequest) {
					inputArgs.append("HttpServletRequest object");
				} else if (signatureArg instanceof MultipartFile) {
					inputArgs.append("File object");
				} else if (signatureArg instanceof Map) {
					Gson gson = new Gson();
					String json = gson.toJson(signatureArg);
					inputArgs.append(json.toString());
				} 
				
				else if (signatureArg instanceof String) {
				    inputArgs.append(signatureArg.toString());}
				else {
					JSONObject jsonObj = new JSONObject(signatureArg);
					inputArgs.append(jsonObj.toString());
				}
			}
		}

		Object result = joinPoint.proceed();

//		logger.info("Method : " + MethodSignature.class.cast(joinPoint.getSignature()).getMethod().getName()
//				+ " | " + "Argument : " + inputArgs + " | " + "Result : " + result + " | " + "Time : "
//				+ (System.currentTimeMillis() - start) + " msec | " + "CorrelationId : " + RequestCorrelation.getId());
//
		logger.info("Method : " + MethodSignature.class.cast(joinPoint.getSignature()).getMethod().getName() + " | "
				+ "Argument : " + inputArgs + " | " + "Result : " + result + " | " + "Time : "
				+ (System.currentTimeMillis() - start) + " msec | " + "CorrelationId : " + RequestCorrelation.getId()
				+ " | "	+ "TenantId : " + RequestCorrelation.getTenentId());

		return result;
	}

}
