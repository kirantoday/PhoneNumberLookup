package com.oxiotest.phonenumberlookup;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.oxiotest.phonenumberlookup.exception.InvalidCountryException;
import com.oxiotest.phonenumberlookup.exception.InvalidPhoneException;
import com.oxiotest.phonenumberlookup.model.PhoneNumberLookupResponse;
import com.oxiotest.phonenumberlookup.service.PhoneNumberLookupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


class PhoneNumberLookupApplicationTests {
    private PhoneNumberLookupServiceImpl service;

    @BeforeEach
    void setup() {service = new PhoneNumberLookupServiceImpl();}

    @Test
    void lookup_validPhoneWithCountryCode_RetrunsCorrectResponse() {
        PhoneNumberLookupResponse result = service.lookupPhoneNumber("+12125690123" , "US");

        assertNotNull(result);
        assertEquals("+12125690123",result.getPhoneNumber());
        assertEquals("US",result.getCountryCode());
        assertNull(result.getError());
    }

    @Test
    void lookup_validPhoneWithPlusSign_RetrunsCorrectResponse() {
        PhoneNumberLookupResponse result = service.lookupPhoneNumber("+442071234567" , "GB");

        assertNotNull(result);
        assertEquals("GB",result.getCountryCode());
        assertNull(result.getError());
    }

    @Test
    void lookup_validPhoneWithSpaces_RetrunsCorrectResponse() {
        PhoneNumberLookupResponse result = service.lookupPhoneNumber("+52 631 3118150" , "MX");

        assertNotNull(result);
        assertEquals("MX",result.getCountryCode());
        assertNull(result.getError());
    }

    @Test
    void lookup_PhoneWithExtraSpaces_ThrowsInvalidPhoneException() {
       assertThrows(InvalidPhoneException.class , () -> service.lookupPhoneNumber("351 21 094  2000" , "PT"));
    }

    @Test
    void lookup_InvalidCountryCodeFormat_ThrowsInvalidCountryException() {
        assertThrows(InvalidCountryException.class , () -> service.lookupPhoneNumber("+12125690123" , "USA"));
    }

    @Test
    void lookup_MismatchedCountryCode_ThrowsInvalidCountryException() {
        assertThrows(InvalidCountryException.class , () -> service.lookupPhoneNumber("+12125690123" , "USA"));
    }

    @Test
    void lookup_PhoneWithoutPlusAndNoCountryCode_ReturnsCorrectResponse() {
        PhoneNumberLookupResponse result = service.lookupPhoneNumber("12125690123" , "");

        assertNotNull(result);
        assertEquals("US",result.getCountryCode());
        assertNull(result.getError());
    }

    @Test
    void lookup_InvalidPhoneWithSpecialCharacters_ThrowsInvalidPhoneException() {
        assertThrows(InvalidPhoneException.class , () -> service.lookupPhoneNumber("+1-212-569-0123" , "US"));
    }

    @Test
    void lookup_EmptyPhoneNumber_ThrowsInvalidPhoneException() {
        assertThrows(InvalidPhoneException.class , () -> service.lookupPhoneNumber("" , ""));
    }

    @Test
    void lookup_ValidPhone_ExtractsAreaCodeCorrectly() {
        PhoneNumberLookupResponse result = service.lookupPhoneNumber("+12025690123", "US");

        assertNotNull(result);
        assertEquals("202", result.getAreaCode());
        assertEquals("5690123", result.getLocalPhoneNumber());
    }

    @Test
    void lookup_lowercaseCountryCode_ThrowsCountryException() {
        assertThrows(InvalidCountryException.class , () -> service.lookupPhoneNumber("+12025690123" , "us"));
    }
}
