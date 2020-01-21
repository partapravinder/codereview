package com.newgen.wrapper.service;

import org.springframework.http.ResponseEntity;

import com.newgen.dto.MetadataDTO;
import com.newgen.exception.CustomException;

public interface MetadataWrapperService {

	public ResponseEntity<String> storeMetadataCore(MetadataDTO metadataDTO);

	public ResponseEntity<String> deleteMetadataCore(String parentId) throws CustomException;

	public ResponseEntity<String> updateMetadataCore(String parentId, String updateFolderParams) throws CustomException;
	
	public ResponseEntity<String> fetchMetadataCore(String parentId) throws CustomException;

}
