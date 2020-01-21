package com.newgen.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newgen.model.StorageConsumption;;

public interface StorageConsumptionDao {
	
	public StorageConsumption findAndRemoveById(String id,String tenantId);
 
	public StorageConsumption findAndModify(String id, HashMap<String, Double> updateContentParams);
	 
	public List<StorageConsumption> findAllContents(Map<String, String[]> paramMap,String tenantId);
	
	//public List<StorageConsumption> findAllContentsByPage(Map<String, String[]> paramMap,String tenantId, int pageNo);
	
	public List<StorageConsumption> findByTenantId(String tenantId);
	
	public StorageConsumption findByTenantIdToday(String tenantId,String date);
	 
    public StorageConsumption insert(StorageConsumption storageConsumption); 
}
