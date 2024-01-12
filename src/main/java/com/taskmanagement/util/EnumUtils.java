package com.taskmanagement.util;


public class EnumUtils {


    /**
     * Parses the provided string value into an enum of the specified type.
     *
     * @param value    The string value to be parsed.
     * @param enumType The class object of the enum type.
     * @param <T>      The type of the enum.
     * @return The parsed enum value.
     * @throws RuntimeException If the provided value is not a valid enum constant.
     */
    public static <T extends Enum<T>> T parseEnum(String value, Class<T> enumType, RuntimeException exception) {
        if (value == null) {
            return null; // If the value is null, return null (optional).
        }
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            throw exception;
        }
    }
}