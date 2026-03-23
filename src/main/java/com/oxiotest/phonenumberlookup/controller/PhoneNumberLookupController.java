package com.oxiotest.phonenumberlookup.controller;

import com.oxiotest.phonenumberlookup.model.PhoneNumberLookupResponse;
import com.oxiotest.phonenumberlookup.service.PhoneNumberLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for phone number lookup operations.
 */
@RestController
@RequestMapping("v1")
public class PhoneNumberLookupController {
    private static final Logger logger = LoggerFactory.getLogger(PhoneNumberLookupController.class);

    private final PhoneNumberLookupService phoneNumberLookupService;

    /**
     * Creates the controller with the lookup service dependency.
     *
     * @param phoneNumberLookupService service used to parse and validate phone numbers
     */
    public PhoneNumberLookupController(PhoneNumberLookupService phoneNumberLookupService){
        this.phoneNumberLookupService = phoneNumberLookupService;
    }

    /**
     * Looks up a phone number and returns its parsed details.
     *
     * @param phoneNumber the phone number to validate and parse
     * @param countryCode optional ISO 3166-1 alpha-2 region code
     * @return the parsed phone number details
     */
    @GetMapping(value = "/phone-numbers")
    public ResponseEntity<PhoneNumberLookupResponse> LookupPhoneNumber(
            @RequestParam(name="phoneNumber",required = true)  String phoneNumber,
            @RequestParam(name="countryCode",required = false)  String countryCode
    ) {
      logger.info("Received phone lookup request for phoneNumber='{}' and countryCode='{}'", phoneNumber, countryCode);
      PhoneNumberLookupResponse response = phoneNumberLookupService.lookupPhoneNumber(phoneNumber,countryCode);
      logger.info("Lookup succeeded for phoneNumber='{}' with resolved countryCode='{}'", phoneNumber, response.getCountryCode());
      return  ResponseEntity.ok(response);
    }
}
