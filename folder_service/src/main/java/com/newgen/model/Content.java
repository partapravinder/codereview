package com.newgen.model;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.newgen.enumdef.Flag;
import com.newgen.enumdef.Privilege;

@Document(collection = "content")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {

	@Id
	private String id;

	private String name;

	private String contentType;

	private String comments;

	private String parentFolderId;

	private String ownerName;

	private String ownerId;

	private String tenantId;

	private String contentLocationId;

	private Privilege privilege;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime;

	@Version
	private long version;

	private Map<String, String> extraFields;

	private Flag flag = Flag.COMMITTED;

	private String token;

	private String noOfPages;
	private String documentType;
	private String documentSize;

	private String metadataId;

	private Map<String, String> metadata;
	private Map<String, String> dataclass;

	public Content() {
		super();
	}

	public Content(String id, String name, String contentType, String comments, String parentFolderId, String ownerName,
			String ownerId, String contentLocationId, Date creationDateTime, Date revisedDateTime, Date accessDateTime,
			String noOfPages, String documentType, String documentSize, String metadataId, Map<String, String> metadata,
			String tenantId, Map<String, String> dataclass) {
		super();
		this.id = id;
		this.name = name;
		this.contentType = contentType;
		this.comments = comments;
		this.parentFolderId = parentFolderId;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.contentLocationId = contentLocationId;
		this.creationDateTime = creationDateTime;
		this.revisedDateTime = revisedDateTime;
		this.accessDateTime = accessDateTime;
		this.setNoOfPages(noOfPages);
		this.setDocumentType(documentType);
		this.setDocumentSize(documentSize);
		this.setMetadata(metadata);
		this.setMetadataId(metadataId);
		this.setTenantId(tenantId);
		this.setDataclass(dataclass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Content [id=" + id + ", name=" + name + ", contentType=" + contentType + ", comments=" + comments
				+ ", parentFolderId=" + parentFolderId + ", ownerName=" + ownerName + ", ownerId=" + ownerId
				+ ", contentLocationId=" + contentLocationId + ", creationDateTime=" + creationDateTime
				+ ", revisedDateTime=" + revisedDateTime + ", accessDateTime=" + accessDateTime + ", version=" + version
				+ ", metadataId=" + metadataId + ", tenantId=" + tenantId + "]";
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the parentFolderId
	 */
	public String getParentFolderId() {
		return parentFolderId;
	}

	/**
	 * @param parentFolderId the parentFolderId to set
	 */
	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the contentLocationId
	 */
	public String getContentLocationId() {
		return contentLocationId;
	}

	/**
	 * @param contentLocationId the contentLocationId to set
	 */
	public void setContentLocationId(String contentLocationId) {
		this.contentLocationId = contentLocationId;
	}

	/**
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	/**
	 * @return the revisedDateTime
	 */
	public Date getRevisedDateTime() {
		return revisedDateTime;
	}

	/**
	 * @param revisedDateTime the revisedDateTime to set
	 */
	public void setRevisedDateTime(Date revisedDateTime) {
		this.revisedDateTime = revisedDateTime;
	}

	/**
	 * @return the accessDateTime
	 */
	public Date getAccessDateTime() {
		return accessDateTime;
	}

	/**
	 * @param accessDateTime the accessDateTime to set
	 */
	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * @return the extraFields
	 */
	@JsonAnyGetter
	public Map<String, String> getExtraFields() {
		return extraFields;
	}

	/**
	 * @param extraFields the extraFields to set
	 */
	public void setExtraFields(Map<String, String> extraFields) {
		this.extraFields = extraFields;
	}

	/**
	 * @return the flag
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the noOfPages
	 */
	public String getNoOfPages() {
		return noOfPages;
	}

	/**
	 * @param noOfPages the noOfPages to set
	 */
	public void setNoOfPages(String noOfPages) {
		this.noOfPages = noOfPages;
	}

	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	/**
	 * @return the documentSize
	 */
	public String getDocumentSize() {
		return documentSize;
	}

	/**
	 * @param documentSize the documentSize to set
	 */
	public void setDocumentSize(String documentSize) {
		this.documentSize = documentSize;
	}

	public String getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(String metadataId) {
		this.metadataId = metadataId;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public Privilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}

	public Map<String, String> getDataclass() {
		return dataclass;
	}

	public void setDataclass(Map<String, String> dataclass) {
		this.dataclass = dataclass;
	}

}
