package com.newgen.factory.impl;

import org.springframework.stereotype.Component;

import com.newgen.exception.CustomException;
import com.newgen.factory.ThirdPartyStorageFactory;
import com.newgen.storage.service.ThirdPartyStorageService;
import com.newgen.storage.service.impl.AWSStorageServiceImpl;
import com.newgen.storage.service.impl.AzureStorageServiceImpl;

@Component
public class ThirdPartyStorageFactoryImpl implements ThirdPartyStorageFactory{
	public ThirdPartyStorageService getStorageService(String type) throws CustomException{
		if(Type.AZURE_BLOB.toString().equalsIgnoreCase(type)){
			return new AzureStorageServiceImpl();
		}else if(Type.AWS.toString().equalsIgnoreCase(type)){
			return new AWSStorageServiceImpl();
		}
		return new AzureStorageServiceImpl();
	}
}
