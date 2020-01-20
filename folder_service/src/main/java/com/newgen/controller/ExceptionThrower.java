package com.newgen.controller;

import org.springframework.http.HttpStatus;

import com.newgen.exception.CustomException;

public class ExceptionThrower {

	public void throwInvalidFolderException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Folder Id", HttpStatus.BAD_REQUEST);
	}
	
	public void throwInvalidTenantException() throws CustomException{
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Invalid Tenant Id", HttpStatus.BAD_REQUEST);
	}
	
	public void throwFolderContentExistsException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "There are folders and contents associated",
				HttpStatus.BAD_REQUEST);
	}
	
	public void throwLockExistsException() throws CustomException {
		throw new CustomException(HttpStatus.LOCKED.value(), "Lock already exists", HttpStatus.LOCKED);
	}

	public void throwInvalidTargetFolderException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Target Folder is a child of the source folder.",
				HttpStatus.BAD_REQUEST);
	}

	public void throwSourceFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Source folder not found!", HttpStatus.BAD_REQUEST);
	}

	
	public void throwTargetFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Target Folder not found.", HttpStatus.BAD_REQUEST);
	}
	
	public void throwVersionConflictException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Version conflict!", HttpStatus.BAD_REQUEST);
	}

	public void throwParentFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Parent folder not found!", HttpStatus.BAD_REQUEST);
	}
	
	public void throwFolderTypeInvalidException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Folder type is not valid", HttpStatus.BAD_REQUEST);
	}
	
	public void throwParentFolderBlankException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Parent folder Id must not be blank!",
				HttpStatus.BAD_REQUEST);
	}
	
	public void throwThisPrivilegeIsNotValidException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Privilege provided is not a valid value!",
				HttpStatus.BAD_REQUEST);
	}
	
	public void throwFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Folder not found!", HttpStatus.BAD_REQUEST);
	}
	
	public void throwCopyFolderNotFoundException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Copy Folder not found.", HttpStatus.BAD_REQUEST);
	}

	public void throwDatabaseFailureException() throws CustomException {
		throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Database connection failure",
				HttpStatus.BAD_REQUEST);
	}

	public void throwFailedToCopyContentError() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Failed to Copy Content", HttpStatus.BAD_REQUEST);
	}

	public void throwFailedToFetchChildrenError() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Failed to fetch the children for folder",
				HttpStatus.BAD_REQUEST);
	}

	public void throwUnknownErrorException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Unknown Error!", HttpStatus.BAD_REQUEST);
	}
	
	public void throwMetadataAbsentException() throws CustomException {
		throw new CustomException(HttpStatus.NOT_FOUND.value(), "No metadata is associated with the requested folder!", HttpStatus.NOT_FOUND);
	}
}
