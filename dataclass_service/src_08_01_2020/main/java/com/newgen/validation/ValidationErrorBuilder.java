package com.newgen.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import com.newgen.model.ValidationError;

public class ValidationErrorBuilder {//NOSONAR

    public static ValidationError fromBindingErrors(Errors errors) {
        ValidationError error = new ValidationError("Validation failed. " + errors.getErrorCount() + " error(s)");
        for (ObjectError objectError : errors.getAllErrors()) {
            error.addValidationError(objectError.getDefaultMessage());
        }
        return error;
    }
}
