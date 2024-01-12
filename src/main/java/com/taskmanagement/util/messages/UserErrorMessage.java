package com.taskmanagement.util.messages;

public interface UserErrorMessage {


    String USER_SAVE_MSG = "Error during saving user";
    String USER_GET_MSG = "Error during getting user";
    String USER_UPDATE_MSG = "Error during updating user";
    String USER_DELETE_MSG = "Error during deleting user";

    String USER_NOT_FOUND_EMAIL_MSG = "User not found with given email";
    String USER_NOT_FOUND_ID_MSG = "User not found with given id";
    String USER_ALREADY_EXISTS_MSG = "User already exists with given email";
    String USER_ALREADY_ACTIVATED_MSG =  "User is already activated";




}
