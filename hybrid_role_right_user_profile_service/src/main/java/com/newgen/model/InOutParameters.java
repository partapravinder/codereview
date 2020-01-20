package com.newgen.model;

import java.util.List;

public class InOutParameters {

	private Double requestPayloadSize;
	private Double responsePayloadSize;
	Folder folder;
	Content content;
	UserProfile userProfile;
	GroupProfile groupProfile;
	List<UserProfile> userProfileList;
	List<GroupProfile> groupProfileList;
	UserGroupAssociation userGroupAssociation;
	GroupUserAssociation groupUserAssociation;
	List<GroupUserAssociation> groupUserAssociationList;
	List<UserGroupAssociation> UserGroupAssociationList;

	public Double getRequestPayloadSize() {
		return requestPayloadSize;
	}

	public void setRequestPayloadSize(Double requestPayloadSize) {
		this.requestPayloadSize = requestPayloadSize;
	}

	public Double getResponsePayloadSize() {
		return responsePayloadSize;
	}

	public void setResponsePayloadSize(Double responsePayloadSize) {
		this.responsePayloadSize = responsePayloadSize;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public GroupProfile getGroupProfile() {
		return groupProfile;
	}

	public void setGroupProfile(GroupProfile groupProfile) {
		this.groupProfile = groupProfile;
	}

	public List<UserProfile> getUserProfileList() {
		return userProfileList;
	}

	public void setUserProfileList(List<UserProfile> userProfileList) {
		this.userProfileList = userProfileList;
	}

	public List<GroupProfile> getGroupProfileList() {
		return groupProfileList;
	}

	public void setGroupProfileList(List<GroupProfile> groupProfileList) {
		this.groupProfileList = groupProfileList;
	}

	public UserGroupAssociation getUserGroupAssociation() {
		return userGroupAssociation;
	}

	public void setUserGroupAssociation(UserGroupAssociation userGroupAssociation) {
		this.userGroupAssociation = userGroupAssociation;
	}

	public GroupUserAssociation getGroupUserAssociation() {
		return groupUserAssociation;
	}

	public void setGroupUserAssociation(GroupUserAssociation groupUserAssociation) {
		this.groupUserAssociation = groupUserAssociation;
	}

	public List<GroupUserAssociation> getGroupUserAssociationList() {
		return groupUserAssociationList;
	}

	public void setGroupUserAssociationList(List<GroupUserAssociation> groupUserAssociationList) {
		this.groupUserAssociationList = groupUserAssociationList;
	}

	public List<UserGroupAssociation> getUserGroupAssociationList() {
		return UserGroupAssociationList;
	}

	public void setUserGroupAssociationList(List<UserGroupAssociation> userGroupAssociationList) {
		UserGroupAssociationList = userGroupAssociationList;
	}

}
