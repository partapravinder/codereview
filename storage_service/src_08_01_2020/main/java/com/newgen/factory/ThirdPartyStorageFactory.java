package com.newgen.factory;

import com.newgen.exception.CustomException;
import com.newgen.storage.service.ThirdPartyStorageService;

public interface ThirdPartyStorageFactory {
	public enum Type {
		AZURE_BLOB, AWS
	}
	ThirdPartyStorageService getStorageService(String type) throws CustomException;
}
