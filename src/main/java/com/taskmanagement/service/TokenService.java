package com.taskmanagement.service;

import com.taskmanagement.exceptions.TokenApiException;
import com.taskmanagement.model.UserEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenService {

    void saveUserToken(UserEntity userEntity, String jwtToken) throws TokenApiException;

    void revokeAllUserTokens(UserEntity user) throws TokenApiException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws TokenApiException, IOException;


}
