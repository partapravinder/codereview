package com.newgen.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.newgen.model.DataClass;

@Component
public class DataClassWriterConverter implements Converter<DataClass, DBObject> {
	@Override
	public DBObject convert(DataClass dataClass) {
		DBObject dbObject = new BasicDBObject();
		/*
		 * if (dataClass.getId() != null) { dbObject.put("_id", dataClass.getId()); }
		 * dbObject.put("folderName", dataClass.getFolderName());
		 * 
		 * dbObject.put("folderType", dataClass.getFolderType());
		 * 
		 * dbObject.put("comments", dataClass.getComments());
		 * 
		 * if (dataClass.getParentFolderId() != null &&
		 * !dataClass.getParentFolderId().isEmpty()) dbObject.put("parentFolder", new
		 * DBRef("folder", dataClass.getParentFolderId()));
		 * 
		 * dbObject.put("ownerName", dataClass.getOwnerName()); dbObject.put("ownerId",
		 * dataClass.getOwnerId()); dbObject.put("creationDateTime",
		 * dataClass.getCreationDateTime()); dbObject.put("revisedDateTime",
		 * dataClass.getRevisedDateTime()); dbObject.put("accessDateTime",
		 * dataClass.getAccessDateTime());
		 * 
		 * dbObject.put("usedFor", dataClass.getUsedFor()); dbObject.put("version",
		 * dataClass.getVersion());
		 * 
		 * if (dataClass.getExtraFields() != null) { for (Map.Entry<String, String>
		 * entry : dataClass.getExtraFields().entrySet()) { dbObject.put(entry.getKey(),
		 * entry.getValue()); } }
		 */
		return dbObject;
	}
	
}
