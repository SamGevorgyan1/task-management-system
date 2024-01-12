package com.taskmanagement.validator;


import com.taskmanagement.exceptions.UserBadRequestException;

public class UserValidator {

    public static void passwordValidator(String password) throws UserBadRequestException {
        if (password.length()< 8){
            throw new UserBadRequestException("Password is short");
        }

        int countOfDigit = 0;
        int countOfUppercase = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isDigit(c)){
                countOfDigit++;
            }else if (Character.isUpperCase(c)){
                countOfUppercase++;
            }
        }

        if (countOfDigit<1&&countOfUppercase<2){
            throw new UserBadRequestException("Password must contain at list one digit and 2 uppercase");
        }
    }

    public static void yearValidator(Integer year) throws UserBadRequestException {
        if (year<1910||year>2023){
            throw new UserBadRequestException("Invalid year");
        }
    }
}
