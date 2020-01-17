package com.newgen.wrapper.service;

import java.util.HashMap;

public interface WrapperAzureSearchService {
	
	public HashMap<String,String> searchPlus(String queryString, String indexName);
	
}
