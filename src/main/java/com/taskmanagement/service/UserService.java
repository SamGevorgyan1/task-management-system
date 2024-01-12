package com.taskmanagement.service;

import com.taskmanagement.dto.requestdto.UserDTO;
import com.taskmanagement.dto.responsedto.UserResponseDTO;
import com.taskmanagement.exceptions.UserApiException;
import com.taskmanagement.exceptions.UserBadRequestException;
import com.taskmanagement.model.UserEntity;
import java.util.List;

public interface UserService {

    UserEntity createUser(UserDTO userEntity) throws UserApiException;

    boolean verifyUser(String verifyCode, String email) throws UserApiException, UserBadRequestException;

    UserEntity findById(Integer id) throws UserApiException;

    UserEntity findByEmail(String email) throws UserApiException, UserBadRequestException;

    List<UserResponseDTO> getAll(String name, String surname, String role) throws UserApiException;

    UserResponseDTO updateUser(Integer id, UserDTO userDTO) throws UserApiException, UserBadRequestException;

    UserResponseDTO updatePassword(String email, String oldPassword, String newPassword, String confirmPassword) throws UserApiException, UserBadRequestException;

    void forgotPassword(String email) throws UserApiException, UserBadRequestException;

    boolean setPassword(String email, String resetToken, String newPassword, String confirmPassword) throws UserApiException, UserBadRequestException;

    void delete(Integer id) throws UserApiException, UserBadRequestException;

    void sendVerificationCode(String email) throws UserApiException, UserBadRequestException;

}