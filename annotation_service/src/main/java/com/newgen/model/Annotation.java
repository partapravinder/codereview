package com.newgen.model;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection = "annotation")
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Annotation {
	
	@Id
	private String id; // not to be updated

	private String annotationName;

	private String annotationGroupName;

	private String annotationBuffer;

	private String annotationData;

	private String annotationType; // not to be updated
	
	private String accessType;
	
	private String documentId;

	private String pageNo;

	private String comments;

	private String tenantId;

	
	private String ownerName; // not to be updated

	private String ownerId; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime; // not to be updated

	private Map<String, String> extraFields;

	public Annotation() {
		super();
	}

	public Annotation(String id) {
		super();
		this.id = id;
	}

	public Annotation(String id, String annotationName, String annotationType, String comments,
			String annotationGroupName, String annotationBuffer, String annotationData, String ownerName,
			String accessType, String documentId, String pageNo, String ownerId, Date creationDateTime, Date revisedDateTime,
			Date accessDateTime,String tenantId) {
		super();
		this.id = id;
		this.annotationGroupName = annotationGroupName;
		this.annotationBuffer = annotationBuffer;
		this.annotationData = annotationData;
		this.annotationName = annotationName;
		this.annotationType = annotationType;
		this.accessType	= accessType;
		this.documentId	= documentId;
		this.pageNo	= pageNo;
		this.comments = comments;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.creationDateTime = creationDateTime;
		this.revisedDateTime = revisedDateTime;
		this.accessDateTime = accessDateTime;
		this.tenantId = tenantId;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Annotation [id=" + id + ", annotationName=" + annotationName + ", annotationType=" + annotationType
				+ ", comments=" + comments + ", annotationGroupName=" + annotationGroupName + ", ownerName=" + ownerName
				+ ", ownerId=" + ownerId +", pageNo=" + pageNo +", documentId=" + documentId + ", creationDateTime=" + creationDateTime + ", revisedDateTime="
				+ revisedDateTime + ", accessDateTime=" + accessDateTime + ", annotationData=" + annotationData
				+ ", annotationBuffer=" + annotationBuffer + ", tenantId=" + tenantId +"]";
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
	 * @return the AnnotationName
	 */
	public String getAnnotationName() {
		return annotationName;
	}

	
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param ownerId
	 *            the ownerId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	/**
	 * @param annotationName the annotationName to set
	 */
	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}

	/**
	 * @return the annotationType
	 */
	public String getAnnotationType() {
		return annotationType;
	}

	/**
	 * @param annotationType the annotationType to set
	 */
	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	}

	public String getAnnotationGroupName() {
		return annotationGroupName;
	}

	public void setAnnotationGroupName(String annotationGroupName) {
		this.annotationGroupName = annotationGroupName;
	}

	public String getAnnotationBuffer() {
		return annotationBuffer;
	}

	public void setAnnotationBuffer(String annotationBuffer) {
		this.annotationBuffer = annotationBuffer;
	}

	public String getAnnotationData() {
		return annotationData;
	}

	public void setAnnotationData(String annotationData) {
		this.annotationData = annotationData;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
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

}
