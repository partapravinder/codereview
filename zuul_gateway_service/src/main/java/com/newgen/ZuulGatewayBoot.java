package com.newgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.newgen.filters.AuthHeaderFilter;
import com.newgen.filters.PostResponseFilter;

//@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ZuulGatewayBoot {
	public static void main(String[] args) {
		SpringApplication.run(ZuulGatewayBoot.class, args);
	}

	@Bean
	public AuthHeaderFilter preFilter() {
		return new AuthHeaderFilter();
	}

	@Bean
	public PostResponseFilter postFilter() {
		return new PostResponseFilter();
	}
	
	 @Bean
	  public FilterRegistrationBean corsFilter() {
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.addAllowedOrigin("*");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("*");
	    source.registerCorsConfiguration("/**", config);
	    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
	    bean.setOrder(0);
	    return bean;
	  }

}