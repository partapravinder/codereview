package com.newgen.dto;

import javax.validation.constraints.NotEmpty;

public class AnnotationDTO {

	//@NotBlank(message = "Annotation Group name must not be blank!")
	private String annotationGroupName;

	private String annotationName;

	//@NotBlank(message = "Annotation Type must not be blank!")
	private String annotationType = "annotation";

	@NotEmpty(message = "Annotation Buffer must not be blank!")
	private StringBuffer annotationBuffer;

	//@NotBlank(message = "Annotation Data must not be blank!")
	private String annotationData;
	
	//@NotEmpty(message = "accessType must not be blank!")
	private String accessType;
	
	@NotEmpty(message = "DocumentId Data must not be blank!")
	private String documentId;

	@NotEmpty(message = "Page No. must not be blank!")
	private String pageNo;

	private String comments;

	//@NotEmpty(message = "Owner name must not be blank!")
	private String ownerName;

	//@NotEmpty(message = "Owner Id must not be blank!")
	private String ownerId;

	/**
	 * @return the annotationType
	 */
	public String getAnnotationType() {
		//annotationType = "annotation";
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

	public StringBuffer getAnnotationBuffer() {
		return annotationBuffer;
	}

	public void setAnnotationBuffer(StringBuffer annotationBuffer) {
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

	
	public String getAnnotationName() {
		return annotationName;
	}

	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
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

}
