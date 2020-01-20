package com.newgen.wrapper.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.newgen.model.StorageCredentials;
import com.newgen.model.StorageLocation;
import com.newgen.model.StorageProcess;
import com.newgen.wrapper.service.WrapperMongoService;

@Component
@Profile({ "development" })
public class MockMongoServiceImpl<T> implements WrapperMongoService<T> {
	private Map<Class<T>, List<T>> beanTListMap = new HashMap<Class<T>, List<T>>();

	@Override
	@SuppressWarnings("unchecked")
	public T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass) {
		List<T> list = new ArrayList<T>();
		if (entityClass.equals(StorageCredentials.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901baa69e489f1624c2c6af");
			storageCredentials.setName("AzureBlob");
			storageCredentials.setStorageProtocol("http");
			storageCredentials.setAccountName("ecmstorage");
			storageCredentials.setAccountKey(
					"0iHxVtKAivA1YnSLatSRhyz3cMJjcguH38mdDU4RZREMoV6zht+Wh/v3u5CcA4DYsxDZUVlS3WqJjKlibFEjCw==");
			storageCredentials.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageCredentials.setVersion(0);
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		} else if (entityClass.equals(StorageLocation.class)) {
			StorageLocation storageLocation = new StorageLocation();
			storageLocation.setId("5901ba739e489f270053fb89");
			storageLocation.setBlobUri("");
			storageLocation.setStorageCredentialId("");
			storageLocation.setContainerName("");
			storageLocation.setType("");
			storageLocation.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setRevisedDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setAccessDateTime(new Date(Long.valueOf("1493285542841")));
			list.add((T) storageLocation);
			beanTListMap.put(entityClass, list);
			return (T) storageLocation;
		} else if (entityClass.equals(StorageProcess.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901ba739e489f270053fb89");
			storageCredentials.setAccountKey("");
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findAndRemove(Query query, Class<T> entityClass) {
		List<T> list = new ArrayList<T>();
		if (entityClass.equals(StorageCredentials.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901baa69e489f1624c2c6af");
			storageCredentials.setName("AzureBlob");
			storageCredentials.setStorageProtocol("http");
			storageCredentials.setAccountName("ecmstorage");
			storageCredentials.setAccountKey(
					"0iHxVtKAivA1YnSLatSRhyz3cMJjcguH38mdDU4RZREMoV6zht+Wh/v3u5CcA4DYsxDZUVlS3WqJjKlibFEjCw==");
			storageCredentials.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageCredentials.setVersion(0);
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		} else if (entityClass.equals(StorageLocation.class)) {
			StorageLocation storageLocation = new StorageLocation();
			storageLocation.setId("5901ba739e489f270053fb89");
			storageLocation.setBlobUri("");
			storageLocation.setStorageCredentialId("");
			storageLocation.setContainerName("");
			storageLocation.setType("");
			storageLocation.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setRevisedDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setAccessDateTime(new Date(Long.valueOf("1493285542841")));
			list.add((T) storageLocation);
			beanTListMap.put(entityClass, list);
			return (T) storageLocation;
		} else if (entityClass.equals(StorageProcess.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901ba739e489f270053fb89");
			storageCredentials.setAccountKey("");
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> find(Query query, Class<T> entityClass) {
		List<T> list = new ArrayList<T>();
		if (entityClass.equals(StorageCredentials.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901baa69e489f1624c2c6af");
			storageCredentials.setName("AzureBlob");
			storageCredentials.setStorageProtocol("http");
			storageCredentials.setAccountName("ecmstorage");
			storageCredentials.setAccountKey(
					"0iHxVtKAivA1YnSLatSRhyz3cMJjcguH38mdDU4RZREMoV6zht+Wh/v3u5CcA4DYsxDZUVlS3WqJjKlibFEjCw==");
			storageCredentials.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageCredentials.setVersion(0);
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
		} else if (entityClass.equals(StorageLocation.class)) {
			StorageLocation storageLocation = new StorageLocation();
			storageLocation.setId("5901ba739e489f270053fb89");
			storageLocation.setBlobUri("");
			storageLocation.setStorageCredentialId("");
			storageLocation.setContainerName("");
			storageLocation.setType("");
			storageLocation.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setRevisedDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setAccessDateTime(new Date(Long.valueOf("1493285542841")));
			list.add((T) storageLocation);
			beanTListMap.put(entityClass, list);
		} else if (entityClass.equals(StorageProcess.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901ba739e489f270053fb89");
			storageCredentials.setAccountKey("");
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
		}

		return beanTListMap.get(entityClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findOne(Query query, Class<T> entityClass) {
		List<T> list = new ArrayList<T>();
		if (entityClass.equals(StorageCredentials.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901baa69e489f1624c2c6af");
			storageCredentials.setName("AzureBlob");
			storageCredentials.setStorageProtocol("http");
			storageCredentials.setAccountName("ecmstorage");
			storageCredentials.setAccountKey(
					"0iHxVtKAivA1YnSLatSRhyz3cMJjcguH38mdDU4RZREMoV6zht+Wh/v3u5CcA4DYsxDZUVlS3WqJjKlibFEjCw==");
			storageCredentials.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageCredentials.setVersion(0);
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		} else if (entityClass.equals(StorageLocation.class)) {
			StorageLocation storageLocation = new StorageLocation();
			storageLocation.setId("5901ba739e489f270053fb89");
			storageLocation.setBlobUri("");
			storageLocation.setStorageCredentialId("");
			storageLocation.setContainerName("");
			storageLocation.setType("");
			storageLocation.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setRevisedDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setAccessDateTime(new Date(Long.valueOf("1493285542841")));
			list.add((T) storageLocation);
			beanTListMap.put(entityClass, list);
			return (T) storageLocation;
		} else if (entityClass.equals(StorageProcess.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901ba739e489f270053fb89");
			storageCredentials.setAccountKey("");
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		}

		return null;
	}

	@Override
	public void insert(BasicDBObject dbObj, String collectionName) {
		// Nothing to be done here
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findAndModify(Query query, Update update, FindAndModifyOptions returnNew, Class<T> entityClass,
			String collectionName) {
		List<T> list = new ArrayList<T>();
		if (entityClass.equals(StorageCredentials.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901baa69e489f1624c2c6af");
			storageCredentials.setName("AzureBlob");
			storageCredentials.setStorageProtocol("http");
			storageCredentials.setAccountName("ecmstorage");
			storageCredentials.setAccountKey(
					"0iHxVtKAivA1YnSLatSRhyz3cMJjcguH38mdDU4RZREMoV6zht+Wh/v3u5CcA4DYsxDZUVlS3WqJjKlibFEjCw==");
			storageCredentials.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageCredentials.setVersion(0);
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		} else if (entityClass.equals(StorageLocation.class)) {
			StorageLocation storageLocation = new StorageLocation();
			storageLocation.setId("5901ba739e489f270053fb89");
			storageLocation.setBlobUri("");
			storageLocation.setStorageCredentialId("");
			storageLocation.setContainerName("");
			storageLocation.setType("");
			storageLocation.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setRevisedDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setAccessDateTime(new Date(Long.valueOf("1493285542841")));
			list.add((T) storageLocation);
			beanTListMap.put(entityClass, list);
			return (T) storageLocation;
		} else if (entityClass.equals(StorageProcess.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901ba739e489f270053fb89");
			storageCredentials.setAccountKey("");
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
		List<T> list = new ArrayList<T>();
		if (entityClass.equals(StorageCredentials.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901baa69e489f1624c2c6af");
			storageCredentials.setName("AzureBlob");
			storageCredentials.setStorageProtocol("http");
			storageCredentials.setAccountName("ecmstorage");
			storageCredentials.setAccountKey(
					"0iHxVtKAivA1YnSLatSRhyz3cMJjcguH38mdDU4RZREMoV6zht+Wh/v3u5CcA4DYsxDZUVlS3WqJjKlibFEjCw==");
			storageCredentials.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageCredentials.setVersion(0);
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		} else if (entityClass.equals(StorageLocation.class)) {
			StorageLocation storageLocation = new StorageLocation();
			storageLocation.setId("5901ba739e489f270053fb89");
			storageLocation.setBlobUri("");
			storageLocation.setStorageCredentialId("");
			storageLocation.setContainerName("");
			storageLocation.setType("");
			storageLocation.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setRevisedDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setAccessDateTime(new Date(Long.valueOf("1493285542841")));
			list.add((T) storageLocation);
			beanTListMap.put(entityClass, list);
			return (T) storageLocation;
		} else if (entityClass.equals(StorageProcess.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901ba739e489f270053fb89");
			storageCredentials.setAccountKey("");
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
			return (T) storageCredentials;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> find(Query query, Class<T> entityClass, String collectionName) {
		List<T> list = new ArrayList<T>();
		if (entityClass.equals(StorageCredentials.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901baa69e489f1624c2c6af");
			storageCredentials.setName("AzureBlob");
			storageCredentials.setStorageProtocol("http");
			storageCredentials.setAccountName("ecmstorage");
			storageCredentials.setAccountKey(
					"0iHxVtKAivA1YnSLatSRhyz3cMJjcguH38mdDU4RZREMoV6zht+Wh/v3u5CcA4DYsxDZUVlS3WqJjKlibFEjCw==");
			storageCredentials.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageCredentials.setVersion(0);
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
		} else if (entityClass.equals(StorageLocation.class)) {
			StorageLocation storageLocation = new StorageLocation();
			storageLocation.setId("5901ba739e489f270053fb89");
			storageLocation.setBlobUri("");
			storageLocation.setStorageCredentialId("");
			storageLocation.setContainerName("");
			storageLocation.setType("");
			storageLocation.setCreationDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setRevisedDateTime(new Date(Long.valueOf("1493285542841")));
			storageLocation.setAccessDateTime(new Date(Long.valueOf("1493285542841")));
			list.add((T) storageLocation);
			beanTListMap.put(entityClass, list);
		} else if (entityClass.equals(StorageProcess.class)) {
			StorageCredentials storageCredentials = new StorageCredentials();
			storageCredentials.setId("5901ba739e489f270053fb89");
			storageCredentials.setAccountKey("");
			list.add((T) storageCredentials);
			beanTListMap.put(entityClass, list);
		}

		return beanTListMap.get(entityClass);
	}

}
