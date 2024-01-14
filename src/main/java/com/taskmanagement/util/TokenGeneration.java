package com.taskmanagement.util;

import org.apache.commons.lang3.RandomStringUtils;

public class TokenGeneration {

    public static String generateVerifyCode() {
        return RandomStringUtils.random(4, true, false);
    }


    public static String generateResetToken() {
        return RandomStringUtils.random(7, false, true);
    }
}