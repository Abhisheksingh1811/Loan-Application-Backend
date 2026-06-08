package com.yasirakbal.secureloanapi.common.utils;

import java.time.LocalDate;
import java.time.Period;

public class DateUtils {
    public static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
