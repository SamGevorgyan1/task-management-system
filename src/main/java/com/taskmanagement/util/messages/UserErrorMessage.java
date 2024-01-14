package com.taskmanagement.util.messages;

public interface UserErrorMessage {

    // General Errors
    String ERROR_CREATING_USER = "Error saving user";
    String ERROR_GETTING_USER = "Error getting user";
    String ERROR_GETTING_USERS = "Error getting users";
    String ERROR_UPDATING_USER = "Error updating user";
    String ERROR_VERIFYING_USER = "Error verifying user";
    String ERROR_CHANGING_PASSWORD_USER = "Error changing password";
    String ERROR_DELETING_USER = "Error deleting user";

    // User Not Found Errors
    String USER_NOT_FOUND_EMAIL = "User not found with the given email";
    String USER_NOT_FOUND_ID = "User not found with the given ID";

    // User Existence Errors
    String USER_ALREADY_EXISTS = "User already exists with the given email";
    String USER_ALREADY_ACTIVATED = "User is already activated";

    // Verification Code Errors
    String VERIFICATION_CODE_LENGTH = "Verification code must be a non-null string with a length of at least 4 characters";
    String INCORRECT_VERIFICATION_CODE = "Incorrect verification code";
    String USER_DOES_NOT_HAVE_VERIFICATION_CODE = "User does not have a verification code";

    // Reset Token Errors
    String RESET_TOKEN_MISMATCH = "Incorrect reset token";

    // Password Errors
    String PASSWORDS_DONT_MATCH = "Passwords don't match";
    String WRONG_OLD_PASSWORD = "Incorrect old password";
    String PASSWORD_INVALID_LENGTH = "Password must be at least 8 characters long";
    String PASSWORD_CONFIRMATION_FAILED = "Password confirmation failed";

    // User ID Error
    String USER_ID_NULL = "User ID cannot be null";

    // User Field Validation Errors
    String USER_EMAIL_NULL_OR_EMPTY = "User email cannot be null or empty";
    String USER_PASSWORD_NULL_OR_MISSING = "User password must be null or missing";
    String USER_EMAIL_NULL_OR_MISSING = "User email must be null or missing";
    String USER_ROLE_NULL_OR_MISSING = "User role must be null or missing";
}
