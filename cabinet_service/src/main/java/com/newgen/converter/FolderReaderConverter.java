package com.newgen.converter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.newgen.model.Folder;

@Component
public class FolderReaderConverter implements Converter<DBObject, Folder> {

	public Folder convert(DBObject source) {
		Folder folder = new Folder();
		@SuppressWarnings("unchecked")
		Map<String, Object> sourceMap = source.toMap();
		
		Map<String, String> extraFields = new HashMap<>();
		
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			if ("_id".equalsIgnoreCase(entry.getKey())) {
				folder.setId(entry.getValue().toString());
			} else if ("folderName".equalsIgnoreCase(entry.getKey())) {
				folder.setFolderName((String) entry.getValue());
			} else if ("folderType".equalsIgnoreCase(entry.getKey())) {
				folder.setFolderType((String) entry.getValue());
			} else if ("comments".equalsIgnoreCase(entry.getKey())) {
				folder.setComments((String) entry.getValue());
			} else if ("parentFolder".equalsIgnoreCase(entry.getKey())) {
				DBRef parentFolder = (DBRef) entry.getValue();
				folder.setParentFolderId(parentFolder.getId().toString());
			} else if ("ownerName".equalsIgnoreCase(entry.getKey())) {
				folder.setOwnerName((String) entry.getValue());
			} else if ("ownerId".equalsIgnoreCase(entry.getKey())) {
				folder.setOwnerId((String) entry.getValue());
			} else if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				folder.setCreationDateTime((Date) entry.getValue());
			} else if ("revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				folder.setRevisedDateTime((Date) entry.getValue());
			} else if ("accessDateTime".equalsIgnoreCase(entry.getKey())) {
				folder.setAccessDateTime((Date) entry.getValue());
			} else if ("usedFor".equalsIgnoreCase(entry.getKey())) {
				folder.setUsedFor((String) entry.getValue());
			} else if ("version".equalsIgnoreCase(entry.getKey())) {
				folder.setVersion((Long) entry.getValue());
			} else {
				extraFields.put(entry.getKey(), (String) entry.getValue());
			}
			
		}
		folder.setExtraFields(extraFields);

		return folder;
	}
	

}
