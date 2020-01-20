package com.newgen.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.newgen.model.DataField;

public class DataClassDTO {

	@Id
	private String id; // dataDefIndex not to be updated

	@NotEmpty(message = "dataClassName must not be blank!")
	private String dataClassName;// dataDefBane

	private String dataDefComment;

	private String ACL;

	private String enableLogFlag;

	private String ACLMoreFlag;

	private String type;

	private String groupId;

	private String unused;

	private String FDFlag;

	private String accessType;

	private List<DataField> dataFields;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date creationDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date revisedDateTime; // not to be updated

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date accessDateTime; // not to be updated

	public DataClassDTO() {
		super();
	}

	public DataClassDTO(String dataDefIndex) {
		super();
		this.id = dataDefIndex;
	}

	public DataClassDTO(String id, String dataClassName, String dataDefComment, String ACL, String enableLogFlag,
			String ACLMoreFlag, String type, Date creationDateTime, String groupId, String unused, String FDFlag,
			String accessType, List<DataField> dataFields) {
		super();
		this.id = id;
		this.dataClassName = dataClassName;
		this.dataDefComment = dataDefComment;
		this.ACL = ACL;
		this.enableLogFlag = enableLogFlag;
		this.ACLMoreFlag = ACLMoreFlag;
		this.type = type;
		this.creationDateTime = creationDateTime;
		this.groupId = groupId;
		this.unused = unused;
		this.FDFlag = FDFlag;
		this.dataFields = dataFields;
	}

	@Override
	public String toString() {
		return "DataClass [id=" + id + ", dataClassName=" + dataClassName + ", dataDefComment=" + dataDefComment
				+ ", ACL=" + ACL + ", enableLogFlag=" + enableLogFlag + ", ACLMoreFlag=" + ACLMoreFlag + ", type="
				+ type + ", creationDateTime=" + creationDateTime + ", revisedDateTime=" + revisedDateTime
				+ ", accessDateTime=" + accessDateTime + ", groupId=" + groupId + ", unused=" + unused + ", FDFlag="
				+ FDFlag + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDataClassName() {
		return dataClassName;
	}

	public void setDataClassName(String dataClassName) {
		this.dataClassName = dataClassName;
	}

	public String getDataDefComment() {
		return dataDefComment;
	}

	public void setDataDefComment(String dataDefComment) {
		this.dataDefComment = dataDefComment;
	}

	public String getACL() {
		return ACL;
	}

	public void setACL(String aCL) {
		ACL = aCL;
	}

	public String getEnableLogFlag() {
		return enableLogFlag;
	}

	public void setEnableLogFlag(String enableLogFlag) {
		this.enableLogFlag = enableLogFlag;
	}

	public String getACLMoreFlag() {
		return ACLMoreFlag;
	}

	public void setACLMoreFlag(String aCLMoreFlag) {
		ACLMoreFlag = aCLMoreFlag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUnused() {
		return unused;
	}

	public void setUnused(String unused) {
		this.unused = unused;
	}

	public String getFDFlag() {
		return FDFlag;
	}

	public void setFDFlag(String fDFlag) {
		FDFlag = fDFlag;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public List<DataField> getDataFields() {
		return dataFields;
	}

	public void setDataFields(List<DataField> dataFields) {
		this.dataFields = dataFields;
	}

	public Date getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Date getRevisedDateTime() {
		return revisedDateTime;
	}

	public void setRevisedDateTime(Date revisedDateTime) {
		this.revisedDateTime = revisedDateTime;
	}

	public Date getAccessDateTime() {
		return accessDateTime;
	}

	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
	}

}
