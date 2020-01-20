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
 * public class UserProfileBulkDTO {
 * 
 * @NotBlank(message = "Tenant ID must not be blank!") private String tenantId;
 * 
 * private List<Profile> profile;
 * 
 * private String userId;
 * 
 * private String groupId;
 * 
 * public UserProfileBulkDTO() {
 * 
 * }
 * 
 * public UserProfileBulkDTO(String tenantId, String userId, String groupId,
 * List<Profile> profile) { this.tenantId = tenantId; this.profile = profile;
 * this.userId = userId; this.groupId = groupId; }
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
 * public List<Profile> getProfile() { return profile; }
 * 
 * public void setProfile(List<Profile> profile) { this.profile = profile; }
 * 
 * public String getUserId() { return userId; }
 * 
 * public void setUserId(String userId) { this.userId = userId; }
 * 
 * public String getGroupId() { return groupId; }
 * 
 * public void setGroupId(String groupId) { this.groupId = groupId; }
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