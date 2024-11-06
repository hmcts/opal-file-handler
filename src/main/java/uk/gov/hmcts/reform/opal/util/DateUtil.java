package uk.gov.hmcts.reform.opal.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String convertToJulian(String date) {
        // Parse the input date in the format YYYY-MM-DD
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Get the last two digits of the year
        String year = String.valueOf(localDate.getYear()).substring(2);

        // Get the day of the year (DDD)
        int dayOfYear = localDate.getDayOfYear();

        // Format the Julian date as YYDDD
        return String.format("%s%03d", year, dayOfYear);
    }

    public static String flipDate(String date) {
        // Parse the input date in the format DDMMYYYY
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("ddMMyyyy"));

        // Format the date as YYYYMMDD
        return localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
