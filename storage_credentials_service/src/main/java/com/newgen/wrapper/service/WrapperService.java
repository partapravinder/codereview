package com.newgen.wrapper.service;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import com.newgen.dto.StorageCredentialDTO;

public interface WrapperService {
	public ResponseEntity<String> createStorageCredentials(StorageCredentialDTO storageCredentialDTO,String tenantId)throws JSONException;

	public ResponseEntity<String> list(String tenantId);

	public ResponseEntity<String> readStorageCredential(String id,String tenantId)throws JSONException;

	public ResponseEntity<String> deleteStorageCredential(String id, String version,String tenantId) throws JSONException;

	public ResponseEntity<String> updateStorageCredential(String updateParams, String id, Long version,String tenantId) throws JSONException;
}
