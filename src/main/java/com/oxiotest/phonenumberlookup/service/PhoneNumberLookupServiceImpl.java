package com.oxiotest.phonenumberlookup.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.oxiotest.phonenumberlookup.exception.InvalidCountryException;
import com.oxiotest.phonenumberlookup.exception.InvalidPhoneException;
import com.oxiotest.phonenumberlookup.model.PhoneNumberLookupResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Default implementation that validates and parses phone numbers using libphonenumber.
 */
@Service
public class PhoneNumberLookupServiceImpl implements PhoneNumberLookupService {
   private static final Logger logger = LoggerFactory.getLogger(PhoneNumberLookupServiceImpl.class);

   private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
   private static final Pattern VALID_PHONENUMBER_PATTERN = Pattern.compile("^(\\+?\\d+)(\\s\\d+)*$");
   private static final Pattern INVALID_MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s{2,}");
   private static final Pattern VALID_COUNTRY_CODE_PATTERN = Pattern.compile("^[A-Z]{2}$");

    /**
     * Validates and parses a phone number into country, area code, and local number components.
     *
     * @param phoneNumber the phone number to parse
     * @param countryCode optional ISO 3166-1 alpha-2 region code
     * @return the parsed phone number details
     */
    @Override
    public PhoneNumberLookupResponse lookupPhoneNumber(String phoneNumber, String countryCode) {
        logger.debug("Starting lookup for phoneNumber='{}' and countryCode='{}'", phoneNumber, countryCode);
        if(phoneNumber == null || phoneNumber.isBlank()) {
            logger.warn("Rejected lookup because phoneNumber is missing or blank");
            throw new InvalidPhoneException("phoneNumber", "required value is missing");
        }
        validateInput(phoneNumber,countryCode);

       Phonenumber.PhoneNumber parsedPhonenumber =  parsePhoneNumber(phoneNumber,countryCode);

       if(!phoneUtil.isValidNumber(parsedPhonenumber)) {
           if(countryCode == null || countryCode.isBlank()) {
               logger.warn("Lookup failed because countryCode is required when the number is not independently valid: phoneNumber='{}'", phoneNumber);
               throw new InvalidPhoneException("countryCode","required value is missing");
           }
           else{
               logger.warn("Lookup failed because parsed number is invalid for phoneNumber='{}' and countryCode='{}'", phoneNumber, countryCode);
               throw new InvalidPhoneException("phoneNumber", "The provided phone number is not valid");
           }
       }
       logger.debug("Lookup completed successfully for phoneNumber='{}'", phoneNumber);
       return extractDetails(phoneNumber,parsedPhonenumber);
    }

    /**
     * Performs basic format validation before libphonenumber parsing.
     *
     * @param phoneNumber the phone number to validate
     * @param countryCode the optional region code to validate
     */
    private void validateInput(String phoneNumber, String countryCode){
        if (INVALID_MULTIPLE_SPACES_PATTERN.matcher(phoneNumber).find()) {
           logger.warn("Rejected phoneNumber='{}' because it contains multiple consecutive spaces", phoneNumber);
           throw new InvalidPhoneException("phoneNumber","Invalid phone number format. The phone number should not contain extra space.");
        }

        if(!VALID_PHONENUMBER_PATTERN.matcher(phoneNumber).matches()) {
            logger.warn("Rejected phoneNumber='{}' because it contains unsupported characters", phoneNumber);
            throw new InvalidPhoneException("phoneNumber","Invalid phone number format. The phone number should contain only digits, spaces, and an optional leading '+'.");
        }

        if(countryCode != null && !countryCode.isBlank()) {
            if(!VALID_COUNTRY_CODE_PATTERN.matcher(countryCode).matches()) {
                logger.warn("Rejected countryCode='{}' because it is not a two-letter uppercase ISO code", countryCode);
                throw new InvalidCountryException("countryCode","Invalid country code format. the country code should be in 2 letter ISO 3166-1 alpha-2 format in Capital Case.");
            }
        }
    }

    /**
     * Parses the phone number using libphonenumber and verifies region consistency.
     *
     * @param phoneNumber the phone number to parse
     * @param countryCode the optional region code
     * @return the parsed phone number
     */
    private Phonenumber.PhoneNumber parsePhoneNumber(String phoneNumber, String countryCode)
    {
        // if CountryCode exists, parse using country code
        if(countryCode != null && !countryCode.isBlank()){
            try {
                Phonenumber.PhoneNumber parsed = phoneUtil.parse(phoneNumber,countryCode);
                String regionCode = phoneUtil.getRegionCodeForNumber(parsed);
                if(regionCode == null || regionCode.isBlank()) {
                    logger.warn("Parsed phoneNumber='{}' but could not determine region for countryCode='{}'", phoneNumber, countryCode);
                    throw new InvalidPhoneException("phoneNumber", "RegionCode is not validated for the input phone number and country code");
                }
                if(!countryCode.equalsIgnoreCase(regionCode)) {
                    logger.warn("Parsed region mismatch for phoneNumber='{}': expected '{}', resolved '{}'", phoneNumber, countryCode, regionCode);
                    throw new InvalidPhoneException("phoneNumber","RegionCode " + regionCode + " and CountryCode " + countryCode + " does not match for the phone number " + phoneNumber);
                }
                return parsed;
            }
            catch (NumberParseException ex){
                logger.warn("Failed to parse phoneNumber='{}' with countryCode='{}': {}", phoneNumber, countryCode, ex.getMessage());
                throw new InvalidPhoneException("phoneNumber", "The Provided phone number is not valid to parse." + ex.getMessage());
            }
        }
        else {
            try{
                String phoneToParse = phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
                logger.debug("Parsing phoneNumber='{}' without explicit country code using normalized value='{}'", phoneNumber, phoneToParse);
                return phoneUtil.parse(phoneToParse,null);
            } catch (NumberParseException e) {
                logger.warn("Failed to parse phoneNumber='{}' without countryCode: {}", phoneNumber, e.getMessage());
                throw new InvalidPhoneException("phoneNumber", "The Provided Phone number is not valid to parse. " + e.getMessage());
            }

        }
    }

    /**
     * Extracts API response fields from the parsed phone number.
     *
     * @param originalPhone the original phone input
     * @param parsedphoneNumber the parsed libphonenumber representation
     * @return the API response payload
     */
    private PhoneNumberLookupResponse extractDetails(String originalPhone, Phonenumber.PhoneNumber parsedphoneNumber){
        String nationalNumber = phoneUtil.getNationalSignificantNumber(parsedphoneNumber);
        int areaCodeLength = phoneUtil.getLengthOfNationalDestinationCode(parsedphoneNumber);
        String areacode = areaCodeLength > 0 ? nationalNumber.substring(0,areaCodeLength) : null;
        String localPhoneNumber = nationalNumber.substring(areaCodeLength);

        PhoneNumberLookupResponse  response = new PhoneNumberLookupResponse();
        response.setPhoneNumber(originalPhone);
        response.setCountryCode(phoneUtil.getRegionCodeForNumber(parsedphoneNumber));
        response.setAreaCode(areacode);
        response.setLocalPhoneNumber(localPhoneNumber);

        logger.debug("Extracted phone details for phoneNumber='{}': countryCode='{}', areaCode='{}'", originalPhone, response.getCountryCode(), areacode);
        return  response;

    }
}
