package com.newgen.filters;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.newgen.model.LogEntity;

public class PostResponseFilter extends ZuulFilter {

	@Autowired
	RestTemplate restTemplate;

	@Value("${logging.service.url}")
	private String loggingUrl;

	@Override
	public String filterType() {
		return POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		System.out.println("In Post Response Filter run method");
		RequestContext context = RequestContext.getCurrentContext();
		System.out.println("context : " + context);
		HttpServletRequest request = context.getRequest();
		// System.out.println(request.getHeader("tenantId").toString());
		// System.out.println(request.getAttribute("start_time"));
		// System.out.println(request.getHeader("userId").toString());
		HttpServletResponse servletResponse = context.getResponse();
		servletResponse.addHeader("X-Sample", UUID.randomUUID().toString());
		// System.out.println("servletResponse : " + servletResponse);
		// long end_time = System.nanoTime();
		// System.out.println("end-time: " + end_time);

		String[] routeHost = context.getRouteHost().toString().split(":");
		String service = routeHost[1].replaceAll("[^a-zA-Z0-9]", "");

		try {
			HttpHeaders headers = new HttpHeaders();

			headers.set("tenantId", request.getHeader("tenantId"));
			headers.set("userId", request.getHeader("userId"));
			headers.set("Content-Type", "application/json");

			String apiurl = loggingUrl + "/logging/saveLog";

			LogEntity logEntity = new LogEntity("Compute", request.getMethod(), service, null, null,
					Long.parseLong(request.getAttribute("start_time").toString()), System.nanoTime());
			HttpEntity<LogEntity> request1 = new HttpEntity<LogEntity>(logEntity, headers);
			restTemplate.exchange(apiurl, HttpMethod.POST, request1, String.class);
		} catch (Exception e) {
			System.out.println("Error in saving log from Zuul");
		}
		return null;
	}
}