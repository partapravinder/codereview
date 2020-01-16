package com.newgen.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.dto.MetadataDTO;
import com.newgen.exception.CustomException;

@Service
public class DTOConvertor {
	
	@SuppressWarnings("unchecked")
	public MetadataDTO getMetadataDTO(String ownerName, String ownerId, String metadataStr) throws CustomException {
		
		MetadataDTO metadataDTO = new MetadataDTO();
		Map<String,String> data =null;
		ObjectMapper mapper = new ObjectMapper();
			
		try {
			data= mapper.readValue(metadataStr.toString(), Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		metadataDTO.setOwnerId(ownerId);
		metadataDTO.setOwnerName(ownerName);
		metadataDTO.setData(data);

		return metadataDTO;
	}

	
}
