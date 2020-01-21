package com.newgen.dao;

import java.util.List;

import com.newgen.model.AsyncFolderOperation;
import com.newgen.model.InOutParameters;
import com.newgen.model.AsyncFolderOperation.Status;

public interface AsyncFolderDao {

	InOutParameters insert(AsyncFolderOperation asyncFolderOperation);

	InOutParameters findById(String id, String tenantId);

	InOutParameters updateStatus(String id, Status status, String tenantId);

	InOutParameters findAndRemoveById(String id, String tenantId);

	List<AsyncFolderOperation> findAllCompletedAndFailed();

	AsyncFolderOperation updateProgress(String id, String tenantId);

}
