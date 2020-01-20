package com.newgen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.newgen.exception.CustomException;

@Component
public class ExceptionThrower {
	public void throwUnknownErrorException() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Unknown Error!", HttpStatus.BAD_REQUEST);
	}

	public void throwGroupIdNotPresent() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Group Id not present for this tenant.",
				HttpStatus.BAD_REQUEST);
	}

	public void throwUserIdNotPresentInThisGroup() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(),
				"User Id(s) not associated with this group for this tenant.", HttpStatus.BAD_REQUEST);
	}

	public void throwNoUserIdNotPresentInThisGroup() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(),
				"No User Id is associated with this group for this tenant.", HttpStatus.BAD_REQUEST);
	}

	public void throwUserIsNotOwnerOfThisObject() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "User Id is not the owner of this object.",
				HttpStatus.BAD_REQUEST);
	}

	public void throwNoUserIdNotPresentInRequest() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "No User Id is present.", HttpStatus.BAD_REQUEST);
	}

	public void throwInvalidObjectType() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Object Type is invalid for one of the Objects.",
				HttpStatus.BAD_REQUEST);
	}

	public void throwFolderDoesNotExist() throws CustomException {
		throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Folder does not exist in this tenant.",
				HttpStatus.BAD_REQUEST);
	}

}
