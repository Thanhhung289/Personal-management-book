package com.group6.moneymanagementbooking.util;

import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isNumberic(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean patternMatchesEmail(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public static boolean checkPhone(String str) {
        String regex = "^(\\d{10,13})$";
        return str.matches(regex);
    }

    public static boolean checkPasswordValidate(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z0-9]{6,}$";
        return password.matches(regex);
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }


    

}
