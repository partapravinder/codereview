package com.newgen.converter;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.newgen.model.StorageLocation;

@Component
public class StorageLocationWriterConverter implements Converter<StorageLocation, DBObject> {
	@Override
	public DBObject convert(StorageLocation storageLocation) {
		DBObject dbObject = new BasicDBObject();
		if (storageLocation.getId() != null) {
			dbObject.put("id", storageLocation.getId());
		}
		dbObject.put("blobUri", storageLocation.getBlobUri());

//		dbObject.put("storageCredentialId", storageLocation.getStorageCredentialId());
		if (storageLocation.getStorageCredentialId() != null && !storageLocation.getStorageCredentialId().isEmpty())
			dbObject.put("storageCredential", new DBRef("storageCredentials", new ObjectId(storageLocation.getStorageCredentialId())));
		dbObject.put("containerName", storageLocation.getContainerName());
		
		dbObject.put("creationDateTime", storageLocation.getCreationDateTime());
		dbObject.put("revisedDateTime", storageLocation.getRevisedDateTime());
		dbObject.put("accessDateTime", storageLocation.getAccessDateTime());

		dbObject.put("version", storageLocation.getVersion());

		if (storageLocation.getExtraFields() != null) {
			for (Map.Entry<String, String> entry : storageLocation.getExtraFields().entrySet()) {
				dbObject.put(entry.getKey(), entry.getValue());
			}
		}
		return dbObject;
	}
}
