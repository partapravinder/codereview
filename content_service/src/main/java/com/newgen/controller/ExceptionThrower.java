package com.newgen.controller;

import org.springframework.http.HttpStatus;

import com.newgen.exception.CustomException;

public class ExceptionThrower {
	public void throwUnknownErrorException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Unknown Error!", HttpStatus.BAD_REQUEST);
	}
	
	public void throwContentNotCheckedOutByParameterUserException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content not checked out by user sent in parameter!", HttpStatus.BAD_REQUEST);
	}
	
	public void throwContentAlreadyCheckedOutException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content already checked out!", HttpStatus.BAD_REQUEST);
	}

	public void throwContentNotCheckedOutException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content not checked out!", HttpStatus.BAD_REQUEST);
	}

	public void throwContentCheckedOutException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content is checked out!", HttpStatus.LOCKED);
	}

	public void throwContentVersionAlreadyLatestException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content version is already latest!", HttpStatus.LOCKED);
	}
	
	public void throwContentVersioningNotValidException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content versioning in the request is not valid!", HttpStatus.BAD_REQUEST);
	}

	public void throwFileEmptyException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Please select a file to upload",
				HttpStatus.BAD_REQUEST);
	}

	public void throwThisPrivilegeIsNotValidException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Not a valid privilege.",
				HttpStatus.BAD_REQUEST);
	}
	
	
	public void throwContentLocationNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content location not found!",
				HttpStatus.BAD_REQUEST);
	}

	public void throwTargetFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Target Folder not found!", HttpStatus.BAD_REQUEST);
	}
	public void throwParentFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Parent folder not found!", HttpStatus.BAD_REQUEST);
	}

	
	public void throwLocationIdNotPresent() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Storage Location Id is not present",
				HttpStatus.BAD_REQUEST);
	}
	public void throwFailedToDeleteStorageLocation() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Failed to delete Storage Location",
				HttpStatus.BAD_REQUEST);
	}
	
	public void throwVersionConflictException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Version conflict!", HttpStatus.BAD_REQUEST);
	}
	
	public void throwLockExistsException() throws CustomException {
		throw new CustomException(HttpStatus.LOCKED.value(), "Lock already exists", HttpStatus.LOCKED);
	}
	
	public void throwInvalidTenantException() throws CustomException{
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Tenant Id", HttpStatus.BAD_REQUEST);
	}
	public void throwFileStoreException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Error in storing file in temp",
				HttpStatus.BAD_REQUEST);
	}

	public void throwContentUploadFailed() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content upload failed", HttpStatus.BAD_REQUEST);
	}

	public void throwStoreContentLocationFailed() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Failed to store content location",
				HttpStatus.BAD_REQUEST);
	}

	public void throwStoreContentMetadataFailed() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Failed to store content metadata",
				HttpStatus.BAD_REQUEST);
	}
	public void throwStoreContentDataclassFailed() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Failed to store content dataclass",
				HttpStatus.BAD_REQUEST);
	}

	public void throwFailedToCreateContentLocation() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Failed to create content location",
				HttpStatus.BAD_REQUEST);
	}

	public void throwContentNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Content not found", HttpStatus.BAD_REQUEST);
	}

	public void throwContentMetadataNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.NOT_FOUND.value(), "No metadata is associated with the requested content!", HttpStatus.NOT_FOUND);
	}
	public void throwContentDataClassNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.NOT_FOUND.value(), "No dataclass is associated with the requested content!", HttpStatus.NOT_FOUND);
	}
	
	public void throwExceeduploadlimitException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Upload Limit Exceeded!", HttpStatus.BAD_REQUEST);
	}

}
