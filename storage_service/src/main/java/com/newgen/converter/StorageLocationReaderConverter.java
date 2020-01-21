package com.newgen.converter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.newgen.model.StorageLocation;

@Component
public class StorageLocationReaderConverter implements Converter<DBObject, StorageLocation> {

	public StorageLocation convert(DBObject source) {
		StorageLocation storageLocation = new StorageLocation();
		@SuppressWarnings("unchecked")
		Map<String, Object> sourceMap = source.toMap();

		Map<String, String> extraFields = new HashMap<>();

		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			if ("_id".equalsIgnoreCase(entry.getKey())) {
				storageLocation.setId(entry.getValue().toString());
			} else if ("blobUri".equalsIgnoreCase(entry.getKey())) {
				storageLocation.setBlobUri((String) entry.getValue());
			} else if ("storageCredential".equalsIgnoreCase(entry.getKey())) {
				DBRef storageCredential = (DBRef) entry.getValue();
				storageLocation.setStorageCredentialId(storageCredential.getId().toString());
			} else if ("containerName".equalsIgnoreCase(entry.getKey())) {
				storageLocation.setContainerName((String) entry.getValue());
			} else if ("creationDateTime".equalsIgnoreCase(entry.getKey())) {
				storageLocation.setCreationDateTime((Date) entry.getValue());
			} else if ("revisedDateTime".equalsIgnoreCase(entry.getKey())) {
				storageLocation.setRevisedDateTime((Date) entry.getValue());
			} else if ("accessDateTime".equalsIgnoreCase(entry.getKey())) {
				storageLocation.setAccessDateTime((Date) entry.getValue());
			} else if ("version".equalsIgnoreCase(entry.getKey())) {
				storageLocation.setVersion((Long) entry.getValue());
			} else {
				extraFields.put(entry.getKey(), (String) entry.getValue());
			}

		}
		storageLocation.setExtraFields(extraFields);

		return storageLocation;
	}
}
