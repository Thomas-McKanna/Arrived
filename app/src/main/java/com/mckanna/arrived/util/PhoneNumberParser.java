package com.mckanna.arrived.util;

public class PhoneNumberParser {

    public static final String INVALID_PHONE_NUMBER = "INVALID_PHONE_NUMBER";

    private static final int parentheticalFormatLength = 14;
    private static final int dashedFormatLength = 12;
    private static final String regexPhoneNumber = "^\\D?(\\d{3})\\D?\\D?(\\d{3})\\D?(\\d{4})$";

    public static String parseNumber(String candidateNumber) {
        String filtered_number = removeCountryCodeIfPresent(candidateNumber);
        String areaCode, prefix, lineNumber;
        if (isValidPhoneNumber(filtered_number)) {
            // (XXX) XXX-XXXX format
            if (filtered_number.length() == parentheticalFormatLength) {
                areaCode = filtered_number.substring(1, 4);
                prefix = filtered_number.substring(6, 9);
                lineNumber = filtered_number.substring(10);
            }
            // XXX-XXX-XXXX format
            else if (filtered_number.length() == dashedFormatLength) {
                areaCode = filtered_number.substring(0, 3);
                prefix = filtered_number.substring(4, 7);
                lineNumber = filtered_number.substring(8);
            }
            // XXXXXXXXXX format
            else {
                areaCode = filtered_number.substring(0, 3);
                prefix = filtered_number.substring(3, 6);
                lineNumber = filtered_number.substring(6);
            }
            return String.format("(%s) %s-%s", areaCode, prefix, lineNumber);
        } else {
            return INVALID_PHONE_NUMBER;
        }
    }

    private static boolean isValidPhoneNumber(String number) {
        number = removeCountryCodeIfPresent(number);
        return number.matches(regexPhoneNumber);
    }

    private static String removeCountryCodeIfPresent(String number) {
        if (number.length() > 2 && startsWithCountryCode(number)) {
            return number.substring(2);
        } else {
            return number;
        }
    }

    private static boolean startsWithCountryCode(String number) {
        String beginning = number.substring(0, 2);
        if (beginning.equals("+1") || beginning.equals("1 ")) {
            return true;
        } else {
            return false;
        }
    }
}
