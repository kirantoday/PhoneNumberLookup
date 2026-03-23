package com.oxiotest.phonenumberlookup.service;

import com.oxiotest.phonenumberlookup.model.PhoneNumberLookupResponse;

/**
 * Contract for looking up and parsing phone number details.
 */
public interface PhoneNumberLookupService {
    /**
     * Validates and parses a phone number.
     *
     * @param phoneNumber the phone number to parse
     * @param countryCode optional ISO 3166-1 alpha-2 region code
     * @return the parsed phone number details
     */
    PhoneNumberLookupResponse lookupPhoneNumber(String phoneNumber, String countryCode);
}
