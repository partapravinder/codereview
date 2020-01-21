package com.newgen.converter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.newgen.model.StorageCredentials;

@Component
public class StorageCredentialWriterConverter implements Converter<StorageCredentials, DBObject> {
	@Override
	public DBObject convert(StorageCredentials storageCredentials) {
		DBObject dbObject = new BasicDBObject();
		if (storageCredentials.getId() != null) {
			dbObject.put("id", storageCredentials.getId());
		}
		dbObject.put("name", storageCredentials.getName());

		dbObject.put("storageProtocol", storageCredentials.getStorageProtocol());

		dbObject.put("accountName", storageCredentials.getAccountName());

		dbObject.put("accountKey", storageCredentials.getAccountKey());
		dbObject.put("creationDateTime", storageCredentials.getCreationDateTime());
		dbObject.put("revisedDateTime", storageCredentials.getRevisedDateTime());
		dbObject.put("accessDateTime", storageCredentials.getAccessDateTime());

		dbObject.put("version", storageCredentials.getVersion());

		if (storageCredentials.getExtraFields() != null) {
			for (Map.Entry<String, String> entry : storageCredentials.getExtraFields().entrySet()) {
				dbObject.put(entry.getKey(), entry.getValue());
			}
		}
		return dbObject;
	}
}
