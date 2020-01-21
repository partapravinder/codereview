package com.newgen.converter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.newgen.model.Folder;

@Component
public class FolderWriterConverter implements Converter<Folder, DBObject> {
	@Override
	public DBObject convert(Folder folder) {
		DBObject dbObject = new BasicDBObject();
		if (folder.getId() != null) {
			dbObject.put("_id", folder.getId());
		}
		dbObject.put("folderName", folder.getFolderName());

		dbObject.put("folderType", folder.getFolderType());

		dbObject.put("comments", folder.getComments());
		
		if (folder.getParentFolderId() != null && !folder.getParentFolderId().isEmpty())
			dbObject.put("parentFolder", new DBRef("folder", folder.getParentFolderId()));

		dbObject.put("ownerName", folder.getOwnerName());
		dbObject.put("ownerId", folder.getOwnerId());
		dbObject.put("creationDateTime", folder.getCreationDateTime());
		dbObject.put("revisedDateTime", folder.getRevisedDateTime());
		dbObject.put("accessDateTime", folder.getAccessDateTime());

		dbObject.put("usedFor", folder.getUsedFor());
		dbObject.put("version", folder.getVersion());

		if (folder.getExtraFields() != null) {
			for (Map.Entry<String, String> entry : folder.getExtraFields().entrySet()) {
				dbObject.put(entry.getKey(), entry.getValue());
			}
		}
		return dbObject;
	}
	
}
