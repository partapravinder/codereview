package com.newgen.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResults {
	List<Folder> folder;
	List<Content> document;
	List<Content> fts;
	ArrayList<FilterItem> keyword;
	ArrayList<FilterItem> dataclass;
	ArrayList<FilterItem> documentType;
	ArrayList<FilterItem> owner;

	public List<Folder> getFolder() {
		return folder;
	}

	public void setFolder(List<Folder> folder) {
		this.folder = folder;
	}

	public List<Content> getDocument() {
		return document;
	}

	public void setDocument(List<Content> document) {
		this.document = document;
	}

	public List<Content> getFts() {
		return fts;
	}

	public void setFts(List<Content> fts) {
		this.fts = fts;
	}

	public ArrayList<FilterItem> getKeyword() {
		return keyword;
	}

	public void setKeyword(ArrayList<FilterItem> keyword) {
		this.keyword = keyword;
	}

	public ArrayList<FilterItem> getDataclass() {
		return dataclass;
	}

	public void setDataclass(ArrayList<FilterItem> dataclass) {
		this.dataclass = dataclass;
	}

	public ArrayList<FilterItem> getDocumentType() {
		return documentType;
	}

	public void setDocumentType(ArrayList<FilterItem> documentType) {
		this.documentType = documentType;
	}

	public ArrayList<FilterItem> getOwner() {
		return owner;
	}

	public void setOwner(ArrayList<FilterItem> owner) {
		this.owner = owner;
	}

}