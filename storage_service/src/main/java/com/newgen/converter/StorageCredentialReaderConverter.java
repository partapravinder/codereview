package com.newgen.converter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;
import com.newgen.model.StorageCredentials;

@Component
public class StorageCredentialReaderConverter implements Converter<DBObject, StorageCredentials> {

	public StorageCredentials convert(DBObject source) {
		StorageCredentials storageCredentials = new StorageCredentials();
		@SuppressWarnings("unchecked")
		Map<String, Object> sourceMap = source.toMap();
		
		Map<String, String> extraFields = new HashMap<>();
		
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			if ("_id".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setId(entry.getValue().toString());
			} else if ("name".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setName((String) entry.getValue());
			} else if ("storageProtocol".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setStorageProtocol((String) entry.getValue());
			} else if ("accountName".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setAccountName((String) entry.getValue());
			} else if ("accountKey".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setAccountKey((String) entry.getValue());
			} else if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setCreationDateTime((Date) entry.getValue());
			} else if ("revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setRevisedDateTime((Date) entry.getValue());
			} else if ("accessDateTime".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setAccessDateTime((Date) entry.getValue());
			} else if ("version".equalsIgnoreCase(entry.getKey())) {
				storageCredentials.setVersion((Long) entry.getValue());
			} else {
				extraFields.put(entry.getKey(), (String) entry.getValue());
			}
		}
		storageCredentials.setExtraFields(extraFields);

		return storageCredentials;
	}
}
