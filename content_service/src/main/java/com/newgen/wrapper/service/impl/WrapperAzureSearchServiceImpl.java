package com.newgen.wrapper.service.impl;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.newgen.wrapper.service.WrapperAzureSearchService;

@Component
@Profile({ "production", "default" })
public class WrapperAzureSearchServiceImpl implements WrapperAzureSearchService{

	@Value("${fts.azure.SearchServiceAdminKey}")
	private String _adminKey;
	
	@Value("${fts.azure.SearchServiceQueryKey}")
    private String _queryKey;
	
	@Value("${fts.azure.ApiVersion}")
    private String _apiVersion;
	
	@Value("${fts.azure.SearchServiceName}")
    private String _serviceName;
	
	@Value("${fts.azure.IndexName}")
    private String _indexName;
	
	public static class SearchOptions {

        public String select = "";
        public String filter = "";
        public int top = 0;
        public String orderby= "";
    }
	
	public HashMap<String,String> searchPlus(String queryString, String indexName) {

        try {
    		RestTemplate restTemplate = new RestTemplate();
    		ResponseEntity<String> response;
    		UriComponentsBuilder builder;
    		builder = UriComponentsBuilder.fromUriString("https://" + _serviceName + ".search.windows.net/indexes/" + indexName + "/docs");
    			
    		builder.queryParam("api-version", _apiVersion);
    		builder.queryParam("search", queryString + "*");
    		builder.queryParam("queryType", "full");
    		HttpHeaders headers = new HttpHeaders();
    		headers.set("content-type", "application/json");
    		headers.set("api-key", _queryKey);
    		HttpEntity req = new HttpEntity<>(headers);
    		response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, req, String.class);
            JsonReader jsonReader = Json.createReader(new StringReader(response.getBody()));
            JsonArray jsonArray = jsonReader.readObject().getJsonArray("value");
            HashMap<String,String> searchedDocuments = parseAzureSearchOutput(jsonArray);
            jsonReader.close();
            return searchedDocuments;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	private HashMap<String,String> parseAzureSearchOutput(JsonArray jsonArray) {
    	HashMap<String,String> searchedDocuments = new HashMap<String, String>();
    	int resultsCount = jsonArray.size();
    	String key;
    	StringBuilder value = null;
        for (int i = 0; i <= resultsCount - 1; i++) {
        	try{
        		value = new StringBuilder();;
	            JsonObject jsonObject = jsonArray.getJsonObject(i);
	            key = jsonObject.getString("metadata_storage_path");
				/*
				 * if(key.length() > 168) { key = key.substring(0, 168); }
				 */
	            
	            Decoder decoder = Base64.getDecoder();
	            byte[] buff = decoder.decode(key);
	            
	            key = (new String(buff)).trim();
	            if(key.endsWith("5")) {
	            	key = key.substring(0, key.length() - 1);
	            }
	            key = URLDecoder.decode(key);
	            
				/*
				 * JsonArray jsonArray1 = jsonObject.getJsonArray("layoutText"); int
				 * resultsCount1 = jsonArray1.size(); for (int i1 = 0; i1 <= resultsCount1 - 1;
				 * i1++) { JsonString jsonObject1 = jsonArray1.getJsonString(i1); JsonReader
				 * reader = Json.createReader(new
				 * StringReader(jsonObject1.toString().substring(1,
				 * jsonObject1.toString().length() -1))); JsonObject object =
				 * (JsonObject)reader.read();
				 * value.append(object.toString()).append(",");//getJsonArray("words").toString(); }
				 * if(value.length() ==0 )
				 */
	            	value.append(jsonObject.getJsonString("merged_content").toString());
	            String strValue = value.toString();
	            if(strValue.charAt(0) == '"')
	            	strValue = strValue.substring(1, strValue.length() - 1).trim();
	            searchedDocuments.put(key, strValue);
        	} catch(Exception ex) {
        		ex.printStackTrace();
        	}
        }
        return searchedDocuments;
    }
}
