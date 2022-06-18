package com.doan.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtils {
    public static LocalDate convertStringToDate(String date){
        String formatDate = "dd/MM/yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        LocalDate dateTime = LocalDate.parse(date, formatter);
        return dateTime;
    }

    public static String convertDateToString(LocalDateTime date){
        String formatDate = "dd/MM/yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        String result = date.format(formatter);
        return result;
    }
}
