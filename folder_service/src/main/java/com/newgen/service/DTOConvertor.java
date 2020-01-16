package com.newgen.service;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.dto.FolderDTO;
import com.newgen.dto.MetadataDTO;
import com.newgen.exception.CustomException;

@Service
public class DTOConvertor {

	public FolderDTO getFolderDTO(String folderstr) throws CustomException, JSONException {
		
		JSONObject folderJson = new JSONObject(folderstr);
		FolderDTO folderDTO = new FolderDTO();
		ObjectMapper mapper = new ObjectMapper();
		
		folderJson.remove("metadataCore");
		
		try {
			folderDTO= mapper.readValue(folderJson.toString(), FolderDTO.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return folderDTO;
	}
	
	
	public MetadataDTO getMetadataDTO(FolderDTO folderDTO) throws CustomException {
		MetadataDTO metadataDTO = new MetadataDTO();
		metadataDTO.setOwnerId(folderDTO.getOwnerId());
		metadataDTO.setOwnerName(folderDTO.getOwnerName());
		metadataDTO.setData(folderDTO.getMetadata());
		return metadataDTO;
	}
	
	@SuppressWarnings("unchecked")
	public MetadataDTO getMetadataDTO(String ownerId, String ownerName, String dataStr) throws CustomException {
		MetadataDTO metadataDTO = new MetadataDTO();
		Map<String,String> data =null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			data= mapper.readValue(dataStr, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		metadataDTO.setOwnerId(ownerId);
		metadataDTO.setOwnerName(ownerName);
		metadataDTO.setData(data);
		return metadataDTO;
	}

}
