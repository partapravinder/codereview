package com.newgen.storage.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.newgen.controller.ExceptionThrower;
import com.newgen.exception.CustomException;
import com.newgen.model.StorageCredentials;
import com.newgen.model.StorageLocation;
import com.newgen.storage.service.ThirdPartyStorageService;

public class AzureStorageServiceImpl extends ExceptionThrower implements ThirdPartyStorageService {
	private static final Logger logger = LoggerFactory.getLogger(AzureStorageServiceImpl.class);

	static final String COMMAND_KEY_UPLOAD = "UploadCommandKey";
	
	static final String COMMAND_KEY_DELETE = "DeleteCommandKey";
	
	static final String DEFAULT_ENDPOINTS_PROTOCOL = "DefaultEndpointsProtocol=";
	
	static final String ACCOUNT_NAME = ";AccountName=";
	
	static final String ACCOUNT_KEY = ";AccountKey=";
	
	@Value("${app.title}")
	String appTitle;
	
	@HystrixCommand(fallbackMethod = "reliableUpload", commandKey = COMMAND_KEY_UPLOAD)
	public String upload(String contentPath, StorageCredentials storageCredentials, String containerName)
			throws Exception {
		logger.debug("Uploading document to Azure Blob " + new Date());
		CloudBlockBlob blob;
		String storageConnectionString = DEFAULT_ENDPOINTS_PROTOCOL + storageCredentials.getStorageProtocol()
				+ ACCOUNT_NAME + storageCredentials.getAccountName() + ACCOUNT_KEY
				+ storageCredentials.getAccountKey();
		logger.debug("Storage connection string: " + storageConnectionString);
		// Retrieve storage account from connection-string.
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
		logger.debug("Retrieved storage account: " + storageAccount);
		// Create the blob client.
		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
		blobClient.getDefaultRequestOptions().setTimeoutIntervalInMs(5000);
		logger.debug("Blob client: " + blobClient);
		// Retrieve reference to a previously created container.
		CloudBlobContainer container;
		container = blobClient.getContainerReference(containerName.toLowerCase());
		logger.debug("Container: " + container.getName());
		// Create the container if it does not exist.
		container.createIfNotExists();
		logger.debug("Container created");
		logger.debug("contentPath: " + contentPath);
		// Create filename to be uploaded on blob
//		String ext = FilenameUtils.getExtension(contentPath);
//		logger.debug("ext: " + ext);
//		String fileName = FilenameUtils.getBaseName(contentPath);
		String fileName = FilenameUtils.getName(contentPath);
		logger.debug("fileName: "+ fileName);
//		String guid = UUID.randomUUID().toString();
//		logger.debug("guid: " +guid);
//		String uploadFileName = fileName + guid + "." + ext;
//		logger.debug("Upload file name: " + uploadFileName);
		blob = container.getBlockBlobReference(fileName);
		logger.debug("step 1 " + blob);
		File file = new File(contentPath);
		logger.debug("step 2" + file);
		FileInputStream fip = new FileInputStream(file);
		blob.upload(fip, file.length());
		logger.debug("Uploaded: " + blob.getUri().toString());
		fip.close();
		logger.debug("Content Uploaded Successfully. Deleting Local file reference from : " + contentPath);
		Path path = Paths.get(contentPath);
		Files.delete(path);
		return blob.getUri().toString();

	}

	public String reliableUpload(String contentPath, StorageCredentials storageCredentials, String containerName)
			throws Exception {
		logger.debug("In reliable upload" + new Date());
		return null;
	}

	@HystrixCommand(fallbackMethod = "reliableDelete", commandKey = COMMAND_KEY_DELETE)
	public String delete(StorageLocation storageLocation, StorageCredentials storageCredentials) throws CustomException, URISyntaxException, StorageException, InvalidKeyException {
		logger.debug("Deleting " + storageLocation.toString());
		String storageConnectionString = DEFAULT_ENDPOINTS_PROTOCOL + storageCredentials.getStorageProtocol()
				+ ACCOUNT_NAME + storageCredentials.getAccountName() + ACCOUNT_KEY
				+ storageCredentials.getAccountKey();
		String fileName = FilenameUtils.getName(storageLocation.getBlobUri()).replace("%20", " ");

		// Retrieve storage account from connection-string.
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Create the blob client.
		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		// Retrieve reference to a previously created container.
		CloudBlobContainer container = blobClient.getContainerReference(storageLocation.getContainerName());

		// Retrieve reference to a blob named "myimage.jpg".
		CloudBlockBlob blob = container.getBlockBlobReference(fileName);

		// Delete the blob.
		blob.deleteIfExists();
		return "";
	}

	public String reliableDelete(String contentPath, StorageCredentials storageCredentials, String containerName)
			throws Exception {
		logger.debug("In reliable delete " + new Date());
		return null;
	}

	public String download(StorageLocation storageLocation, StorageCredentials storageCredentials) throws CustomException, URISyntaxException, StorageException, InvalidKeyException {
		logger.debug("Downloading " + storageLocation.toString());
		String documentURI;
		String storageConnectionString = "DefaultEndpointsProtocol=" + storageCredentials.getStorageProtocol()
				+ ";AccountName=" + storageCredentials.getAccountName() + ";AccountKey="
				+ storageCredentials.getAccountKey();

		String fileName = FilenameUtils.getName(storageLocation.getBlobUri());
		// Retrieve storage account from connection-string.
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Create the blob client.
		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		// Retrieve reference to a previously created container.
		CloudBlobContainer container = blobClient.getContainerReference(storageLocation.getContainerName());

		// Create a new shared access policy.
		SharedAccessBlobPolicy sharedAccessBlobPolicy = new SharedAccessBlobPolicy();

		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR,8);
		sharedAccessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());

		// Set READ and WRITE permissions.
		sharedAccessBlobPolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ));

		/* Create or overwrite the "myimage.jpg" blob with contents from a
		 local file.*/
		CloudBlockBlob blob = container.getBlockBlobReference(fileName);

		BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
		/* Private container with no access for anonymous users
		 containerPermissions.setPublicAccess(BlobContainerPublicAccessType.OFF);
		 Name the shared access policy: heath*/
		containerPermissions.getSharedAccessPolicies().put("heath", sharedAccessBlobPolicy);
		container.uploadPermissions(containerPermissions);
		// Generate the policy SAS string for heath access
		String sas = container.generateSharedAccessSignature(new SharedAccessBlobPolicy(), "heath");

		documentURI = blob.getUri().toString() + "?" + sas;
		//logger.debug("------------" +documentURI.replace("%2520", "%20"));
		return documentURI.replace("%2520", "%20");
	}
}
