package com.taskmanagement.service;

import com.taskmanagement.dto.requestdto.AuthenticationDTO;
import com.taskmanagement.dto.requestdto.UserDTO;
import com.taskmanagement.dto.responsedto.AuthenticationResponseDTO;
import com.taskmanagement.exceptions.AuthApiException;
import com.taskmanagement.exceptions.AuthBadRequestException;
import com.taskmanagement.exceptions.UserApiException;
import com.taskmanagement.exceptions.UserBadRequestException;

public interface AuthService {

     AuthenticationResponseDTO login (AuthenticationDTO authenticationDTO) throws AuthApiException, AuthBadRequestException, UserBadRequestException;

     boolean register(UserDTO userDTO) throws AuthApiException, UserBadRequestException;

}