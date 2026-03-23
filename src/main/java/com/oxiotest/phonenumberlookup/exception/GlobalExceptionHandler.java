package com.oxiotest.phonenumberlookup.exception;

import com.oxiotest.phonenumberlookup.model.PhoneNumberLookupResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Maps application exceptions to HTTP error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles invalid phone input errors.
     *
     * @param ex the thrown validation exception
     * @param request the current HTTP request
     * @return a 400 response containing the validation details
     */
    @ExceptionHandler(InvalidPhoneException.class)
    public ResponseEntity<PhoneNumberLookupResponse> handleInvalidPhone(
            InvalidPhoneException ex, HttpServletRequest request) {
        String phoneNumber = request.getParameter("phoneNumber");
        logger.warn("Phone validation failed for phoneNumber='{}': {} -> {}", phoneNumber, ex.getErrorField(), ex.getValidationMessage());
        PhoneNumberLookupResponse response = buildErrorResponse(phoneNumber, ex.getErrorField(),ex.getValidationMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles invalid country code errors.
     *
     * @param ex the thrown validation exception
     * @param request the current HTTP request
     * @return a 400 response containing the validation details
     */
    @ExceptionHandler(InvalidCountryException.class)
    public ResponseEntity<PhoneNumberLookupResponse> handleInvalidCountry(
            InvalidCountryException ex, HttpServletRequest request) {
        String phoneNumber = request.getParameter("phoneNumber");
        logger.warn("Country validation failed for phoneNumber='{}': {} -> {}", phoneNumber, ex.getErrorField(), ex.getValidationMessage());
        PhoneNumberLookupResponse response = buildErrorResponse(phoneNumber, ex.getErrorField(),ex.getValidationMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Builds the common error response payload returned by the API.
     *
     * @param phoneNumber the original request phone number
     * @param errorField the field that failed validation
     * @param errorMessage the associated validation message
     * @return response payload containing the error details
     */
    public PhoneNumberLookupResponse buildErrorResponse(String phoneNumber, String errorField, String errorMessage) {
        PhoneNumberLookupResponse errorResponse = new PhoneNumberLookupResponse();
        errorResponse.setPhoneNumber(phoneNumber);
        errorResponse.SetError(Map.of(errorField,errorMessage));
        return errorResponse;
    }


}
