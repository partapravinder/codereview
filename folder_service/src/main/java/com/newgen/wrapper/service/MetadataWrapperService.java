package com.newgen.wrapper.service;

import org.springframework.http.ResponseEntity;

import com.newgen.dto.MetadataDTO;
import com.newgen.exception.CustomException;

public interface MetadataWrapperService {

	public ResponseEntity<String> storeMetadataCore(MetadataDTO metadataDTO);

	public ResponseEntity<String> deleteMetadataCore(String id) throws CustomException;

	public ResponseEntity<String> updateMetadataCore(String id, String updatedMetadata) throws CustomException;
	
	public ResponseEntity<String> fetchMetadataCore(String id) throws CustomException;

}
