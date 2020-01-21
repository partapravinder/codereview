/*
 * package com.newgen.dto;
 * 
 * import java.util.Date; import java.util.List;
 * 
 * import javax.validation.constraints.NotBlank;
 * 
 * import org.springframework.format.annotation.DateTimeFormat; import
 * org.springframework.format.annotation.DateTimeFormat.ISO;
 * 
 * import com.newgen.model.Profile;
 * 
 * public class BulkUserProfileDTO {
 * 
 * @NotBlank(message = "Tenant ID must not be blank!") private String tenantId;
 * 
 * private List<Profile> profile;
 * 
 * private List<String> userIdList;
 * 
 * private String parentFolderId;
 * 
 * public BulkUserProfileDTO() {
 * 
 * }
 * 
 * public BulkUserProfileDTO(String tenantId, List<String> userIdList, String
 * parentFolderId, List<Profile> profile) { this.tenantId = tenantId;
 * this.profile = profile; this.userIdList = userIdList; this.parentFolderId =
 * parentFolderId; }
 * 
 * @DateTimeFormat(iso = ISO.DATE_TIME) private Date creationDateTime; // not to
 * be updated
 * 
 * @DateTimeFormat(iso = ISO.DATE_TIME) private Date revisedDateTime; // not to
 * be updated
 * 
 * @DateTimeFormat(iso = ISO.DATE_TIME) private Date accessDateTime; // not to
 * be updated
 * 
 * public String getTenantId() { return tenantId; }
 * 
 * public void setTenantId(String tenantId) { this.tenantId = tenantId; }
 * 
 * public List<String> getUserIdList() { return userIdList; }
 * 
 * public void setUserIdList(List<String> userIdList) { this.userIdList =
 * userIdList; }
 * 
 * public String getParentFolderId() { return parentFolderId; }
 * 
 * public void setParentFolderId(String parentFolderId) { this.parentFolderId =
 * parentFolderId; }
 * 
 * public List<Profile> getProfile() { return profile; }
 * 
 * public void setProfile(List<Profile> profile) { this.profile = profile; }
 * 
 * public Date getCreationDateTime() { return creationDateTime; }
 * 
 * public void setCreationDateTime(Date creationDateTime) {
 * this.creationDateTime = creationDateTime; }
 * 
 * public Date getRevisedDateTime() { return revisedDateTime; }
 * 
 * public void setRevisedDateTime(Date revisedDateTime) { this.revisedDateTime =
 * revisedDateTime; }
 * 
 * public Date getAccessDateTime() { return accessDateTime; }
 * 
 * public void setAccessDateTime(Date accessDateTime) { this.accessDateTime =
 * accessDateTime; }
 * 
 * }
 */