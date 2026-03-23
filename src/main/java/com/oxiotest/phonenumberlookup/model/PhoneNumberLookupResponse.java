package com.oxiotest.phonenumberlookup.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Response payload returned by the phone lookup API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneNumberLookupResponse {
    private String phoneNumber;
    private String countryCode;
    private String areaCode;
    private String localPhoneNumber;
    private Map<String,String> error;

    /**
     * Creates an empty response object.
     */
    public PhoneNumberLookupResponse()  {}

    /**
     * @return the original phone number from the request
     */
    public String getPhoneNumber() {return phoneNumber;}

    /**
     * @param phoneNumber the original phone number from the request
     */
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

    /**
     * @return the resolved ISO country code
     */
    public  String getCountryCode() {return  countryCode;}

    /**
     * @param countryCode the resolved ISO country code
     */
    public void setCountryCode(String countryCode) {this.countryCode = countryCode;}

    /**
     * @return the extracted area code, if available
     */
    public String getAreaCode() {return areaCode;}

    /**
     * @param areaCode the extracted area code
     */
    public void setAreaCode(String areaCode) {this.areaCode = areaCode;}

    /**
     * @return the extracted local phone number portion
     */
    public String getLocalPhoneNumber() {return  localPhoneNumber;}

    /**
     * @param localPhoneNumber the extracted local phone number portion
     */
    public void setLocalPhoneNumber(String localPhoneNumber) {this.localPhoneNumber = localPhoneNumber;}

    /**
     * @return validation errors when the request is invalid
     */
    public  Map<String,String> getError() {return error;}

    /**
     * @param error validation errors when the request is invalid
     */
    public void SetError(Map<String,String> error) {this.error = error;}

}
