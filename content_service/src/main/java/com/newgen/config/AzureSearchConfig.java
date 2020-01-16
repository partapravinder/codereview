package com.newgen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.newgen.ecm.azure.search.SearchServiceClient;
import com.newgen.ecm.azure.search.SearchServiceHelper;

@Configuration
public class AzureSearchConfig {

	@Value("${easysearch.azure.searchServiceName:revampsearch}")
	String searchServiceName;

	@Value("${easysearch.azure.searchServiceAPIVersion:2015-02-28-Preview}")
	String searchServiceAPIVersion;

	@Value("${easysearch.azure.searchServiceAPIKey:DD9ECE452D0B348DA1A895DC506CC410}")
	String searchServiceAPIKey;

	@Bean
	public SearchServiceClient searchServiceClient() {
		return new SearchServiceClient();
	}

	@Bean
	public SearchServiceHelper searchServiceHelper() {
		return new SearchServiceHelper(searchServiceName, searchServiceAPIVersion, searchServiceAPIKey);
	}
}
