/*
 * package com.newgen.wrapper.service.impl;
 * 
 * import org.json.JSONException; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.context.annotation.Profile; import
 * org.springframework.http.HttpEntity; import
 * org.springframework.http.HttpHeaders; import
 * org.springframework.http.HttpMethod; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.stereotype.Component; import
 * org.springframework.web.client.RestTemplate; import
 * org.springframework.web.util.UriComponentsBuilder;
 * 
 * import com.newgen.dto.FolderDTO; import
 * com.newgen.wrapper.service.WrapperService;
 * 
 * @Component
 * 
 * @Profile({ "production", "default" }) public class WrapperServiceImpl
 * implements WrapperService {
 * 
 * @Autowired private RestTemplate restTemplate;
 * 
 * // @Autowired // private EurekaUrlResolver eurekaUrlResolver; //
 * // @Value("${service.folder.serviceId}") // private String folderServiceId;
 * 
 * @Value("${folder.service.url}") private String folderServiceUrl;
 * 
 * private String folderApiPath = "/folders";
 * 
 * @Override public ResponseEntity<String> deleteChildFolders(String
 * folderId,String version, String tenantId) throws JSONException {
 * 
 * HttpHeaders headers = new HttpHeaders(); //String folderServiceUrl =
 * eurekaUrlResolver.procureUrl(folderServiceId); // Query parameters
 * UriComponentsBuilder builder =
 * UriComponentsBuilder.fromUriString(folderServiceUrl + folderApiPath + "/" +
 * folderId); // Add query parameter if (version != null) {
 * builder.queryParam("version", version); } headers.set("tenantId", tenantId);
 * headers.set("jwt",
 * "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlY21uZXh0YWRtaW4iLCJ1c2VySWQiOiI4OTc2MzIzIiwicm9sZSI6ImFjY291bnRhZG1pbiJ9.dKgYzDxmke505lhJA-basgbPpvtqFDpD79Bc2X24QjC5qTq0vKGD9IZG3-mL4YbN-QgUBLxVnh73N9tp9nd0zg"
 * ); builder.queryParam("recursive", true); HttpEntity<FolderDTO> request = new
 * HttpEntity<>(headers); return restTemplate.exchange(builder.toUriString(),
 * HttpMethod.DELETE, request, String.class); }
 * 
 * }
 */