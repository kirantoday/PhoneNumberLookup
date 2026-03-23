package com.oxiotest.phonenumberlookup.exception;

/**
 * Exception raised when a country code fails validation.
 */
public class InvalidCountryException extends RuntimeException{
    private final String errorField;
    private final String validationMessage;

    /**
     * Creates an invalid country exception.
     *
     * @param errorField the field that failed validation
     * @param validationMessage the validation error message
     */
    public InvalidCountryException(String errorField,String validationMessage) {
        super(validationMessage);
        this.errorField = errorField;
        this.validationMessage = validationMessage;
    }

    /**
     * @return the field that failed validation
     */
    public String getErrorField() {return errorField;}

    /**
     * @return the validation error message
     */
    public String getValidationMessage() {return  validationMessage;}
}
