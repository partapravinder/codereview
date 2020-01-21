package com.newgen.filters;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class AuthHeaderFilter extends ZuulFilter {

	@Value("${corrus.auth.url}")
	private String authUrl;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public String filterType() {
		return PRE_TYPE;
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
	public Object run() throws ZuulException {
		
		long start_time = System.nanoTime();
		System.out.println("start_time " + start_time);

		RequestContext ctx = RequestContext.getCurrentContext();
		
		/*
		 * ctx.getResponse().addHeader("Access-Control-Allow-Origin", "*"); if
		 * (ctx.getRequest().getHeader("Access-Control-Request-Method") != null &&
		 * "OPTIONS".equals(ctx.getRequest().getMethod())) {
		 * //LOG.trace("Sending Header...."); // CORS "pre-flight" request
		 * ctx.getResponse().addHeader("Access-Control-Allow-Methods",
		 * "GET, POST, PUT, DELETE"); //
		 * response.addHeader("Access-Control-Allow-Headers", // "Authorization");
		 * ctx.getResponse().addHeader("Access-Control-Allow-Headers",
		 * "Content-Type, authToken, timeZoneOffset, wkwebview, Accept, tenantId, org");
		 * ctx.getResponse().addHeader("Access-Control-Max-Age", "1");
		 * ctx.getResponse().setStatus(200); return null; }
		 */
		
		HttpServletRequest request = ctx.getRequest();
		request.setAttribute("start_time", start_time);

		System.out.println(
				"Request Method : " + request.getMethod() + " Request URL : " + request.getRequestURL().toString());

		if (!(StringUtils.isEmpty(request.getHeader("org"))) && request.getHeader("org").equalsIgnoreCase("ECM")) {

		} else if (request.getHeader("authToken") != null) {
			System.out.println("AuthToken: " + request.getHeader("authToken").toString());
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(authUrl);
			builder.queryParam("authToken", request.getHeader("authToken").toString());

			// HttpHeaders headers = new HttpHeaders();
			// headers.setContentType(MediaType.APPLICATION_JSON);
			// HttpEntity<String> authRequest = new HttpEntity<>(null, headers);
			JSONObject resJson = null;
			ResponseEntity<String> res = restTemplate.getForEntity(builder.toUriString(), String.class);
			System.out.println(res.getBody().toString());
			try {
				resJson = new JSONObject(res.getBody());
				if (resJson.get("statusCode").equals("0") || (resJson.getString("statusMessage").equals("Failure"))) {
					// blocks the request
					ctx.setSendZuulResponse(false);
					// response to client
					ctx.setResponseBody(
							"{\"Error\":\"Invalid authToken\",\"ErrorMessage\":\"authToken is invalid or may have expired.\"}\r\n");
					ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
					ctx.getResponse().setHeader("Content-Type", "application/json");
				} else {
					// Valid authToken.
					// Forward the request to downstream services
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// ctx.unset();
			ctx.setSendZuulResponse(false);
			ctx.setResponseBody(
					"{\"Error\":\"Invalid User/Client\",\"ErrorMessage\":\"Requested resource/operation requires a valid authToken.\"}\r\n");
			ctx.setResponseStatusCode(HttpStatus.PRECONDITION_REQUIRED.value());
			ctx.getResponse().setHeader("Content-Type", "application/json");
			// ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
		}

		return null;
	}
}
