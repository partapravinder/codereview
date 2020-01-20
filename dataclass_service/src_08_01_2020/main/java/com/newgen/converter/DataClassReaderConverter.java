package com.newgen.converter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;
import com.newgen.model.DataClass;

@Component
public class DataClassReaderConverter implements Converter<DBObject, DataClass> {

	public DataClass convert(DBObject source) {
		DataClass dataClass = new DataClass();
		@SuppressWarnings("unchecked")
		Map<String, Object> sourceMap = source.toMap();
		
		Map<String, String> extraFields = new HashMap<>();
		
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			/*
			 * if ("_id".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setId(entry.getValue().toString()); } else if
			 * ("folderName".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setFolderName((String) entry.getValue()); } else if
			 * ("folderType".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setFolderType((String) entry.getValue()); } else if
			 * ("comments".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setComments((String) entry.getValue()); } else if
			 * ("parentFolder".equalsIgnoreCase(entry.getKey())) { DBRef parentFolder =
			 * (DBRef) entry.getValue();
			 * dataClass.setParentFolderId(parentFolder.getId().toString()); } else if
			 * ("ownerName".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setOwnerName((String) entry.getValue()); } else if
			 * ("ownerId".equalsIgnoreCase(entry.getKey())) { dataClass.setOwnerId((String)
			 * entry.getValue()); } else if
			 * ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setCreationDateTime((Date) entry.getValue()); } else if
			 * ("revisedDateTime".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setRevisedDateTime((Date) entry.getValue()); } else if
			 * ("accessDateTime".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setAccessDateTime((Date) entry.getValue()); } else if
			 * ("usedFor".equalsIgnoreCase(entry.getKey())) { dataClass.setUsedFor((String)
			 * entry.getValue()); } else if ("version".equalsIgnoreCase(entry.getKey())) {
			 * dataClass.setVersion((Long) entry.getValue()); } else {
			 * extraFields.put(entry.getKey(), (String) entry.getValue()); }
			 * 
			 */}
		//dataClass.setExtraFields(extraFields);

		return dataClass;
	}
	

}
