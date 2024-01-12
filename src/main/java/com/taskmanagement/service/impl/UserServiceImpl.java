package com.taskmanagement.service.impl;

import com.taskmanagement.dto.requestdto.UserDTO;
import com.taskmanagement.dto.responsedto.UserResponseDTO;
import com.taskmanagement.enums.UserRole;
import com.taskmanagement.enums.UserStatus;
import com.taskmanagement.exceptions.UserAlreadyExistsException;
import com.taskmanagement.exceptions.UserApiException;
import com.taskmanagement.exceptions.UserBadRequestException;
import com.taskmanagement.exceptions.UserNotFoundException;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.UserService;
import com.taskmanagement.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.taskmanagement.util.TokenGeneration.generateResetToken;
import static com.taskmanagement.util.TokenGeneration.generateVerifyCode;
import static com.taskmanagement.util.converters.UserDTOConverter.convertUserEntitiesToDTOS;
import static com.taskmanagement.util.converters.UserDTOConverter.convertUserEntityToDTO;
import static com.taskmanagement.util.messages.UserErrorMessage.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    //private final TaskEmailSender taskEmailSender;

    @Override
    public UserEntity createUser(UserDTO userDTO) throws UserApiException {

        UserEntity userByEmail;
        try {
            userByEmail = userRepository.findByEmail(userDTO.getEmail());
        } catch (Exception e) {
            throw new UserApiException(USER_SAVE_MSG);
        }
        if (userByEmail != null) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_MSG);
        }

        UserEntity user = buildUserEntity(userDTO);
        user.setVerifyCode(generateVerifyCode());

        //taskEmailSender.sendEmail(userDTO.getEmail(), "asd", "asd");

        return saveUser(user, USER_SAVE_MSG);
    }

    @Override
    public boolean verifyUser(String verifyCode, String email) throws UserApiException, UserBadRequestException {

        if (verifyCode == null || verifyCode.length() < 4) {
            throw new UserBadRequestException("Verification code must be a non-null string with a length of at least 4 characters");
        }

        UserEntity user = findByEmail(email);

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserBadRequestException(USER_ALREADY_ACTIVATED_MSG);
        }

        if (user.getVerifyCode() == null) {
            throw new UserBadRequestException("User does not have a verification code");
        }

        if (!user.getVerifyCode().equals(verifyCode)) {
            throw new UserBadRequestException("Incorrect verification code");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setVerifyCode(null);
        saveUser(user, "Error during verifying user");
        return true;
    }

    @Override
    public UserEntity findByEmail(String email) throws UserApiException, UserBadRequestException {

        UserEntity userEntity;

        if (email == null || email.isBlank()) {
            throw new UserBadRequestException("User email can not be null or empty");
        }

        try {
            userEntity = userRepository.findByEmail(email);
        } catch (Exception e) {
            throw new UserApiException(USER_GET_MSG);
        }
        if (userEntity == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_EMAIL_MSG + email);
        }
        return userEntity;
    }

    @Override
    public UserEntity findById(Integer id) throws UserApiException {

        Optional<UserEntity> userEntity;

        try {
            userEntity = userRepository.findById(id);
        } catch (Exception e) {
            throw new UserApiException(USER_GET_MSG);
        }
        if (userEntity.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_ID_MSG);
        }
        return userEntity.get();
    }


    @Override
    @Secured({"PROJECT_ADMIN"})
    public List<UserResponseDTO> getAll(String name, String surname, String role) throws UserApiException {
        List<UserEntity> userEntities;

        UserRole userRole = null;

        if (role != null) {
            userRole = Enum.valueOf(UserRole.class, role);
        }

        try {
            if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(surname) && !StringUtils.isEmpty(role)) {
                userEntities = userRepository.getByNameAndSurnameAndRole(name, surname, userRole);
            } else if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(surname)) {
                userEntities = userRepository.getByNameAndSurname(name, surname);
            } else if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(role)) {
                userEntities = userRepository.getByNameAndRole(name, userRole);
            } else if (!StringUtils.isEmpty(surname) && !StringUtils.isEmpty(role)) {
                userEntities = userRepository.getBySurnameAndRole(name, userRole);
            } else if (!StringUtils.isEmpty(name)) {
                userEntities = userRepository.getByName(name);
            } else if (!StringUtils.isEmpty(role)) {
                userEntities = userRepository.getByRole(userRole);
            } else if (!StringUtils.isEmpty(surname)) {
                userEntities = userRepository.getBySurname(surname);
            } else {
                userEntities = userRepository.findAll();
            }
        } catch (Exception e) {
            throw new UserApiException(USER_GET_MSG);
        }

        return convertUserEntitiesToDTOS(userEntities);
    }

    @Override
    public UserResponseDTO updateUser(Integer id, UserDTO userDTO) throws UserApiException, UserBadRequestException {
        if (id == null) {
            throw new UserBadRequestException("User id can not be null");
        }

        if (userDTO.getPassword() != null) {
            throw new UserBadRequestException("User password must be null or missing");
        }
        if (userDTO.getEmail() != null) {
            throw new UserBadRequestException("User email must be null or missing");
        }
        if (userDTO.getRole() != null) {
            throw new UserBadRequestException("User role must be null or missing");
        }

        UserValidator.yearValidator(userDTO.getYear());

        Optional<UserEntity> userEntity;
        UserEntity updatedUser;
        try {
            userEntity = userRepository.findById(id);
        } catch (Exception e) {
            throw new UserApiException(USER_UPDATE_MSG);
        }
        if (userEntity.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_ID_MSG);
        }

        UserEntity user = userEntity.get();

        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setYear(userDTO.getYear());


        try {
            updatedUser = userRepository.save(user);
        } catch (Exception e) {
            throw new UserApiException(USER_UPDATE_MSG);
        }


        return convertUserEntityToDTO(updatedUser);
    }

    @Override
    public UserResponseDTO updatePassword(String email, String oldPassword, String newPassword, String confirmPassword) throws UserApiException, UserBadRequestException {
        if (!StringUtils.isEmpty(newPassword) && !newPassword.equals(confirmPassword)) {
            throw new UserBadRequestException("Passwords dont matches");
        }
        UserEntity userEntity;
        try {
            userEntity = findByEmail(email);
        } catch (Exception e) {
            throw new UserApiException("Error during changing password");
        }

        if (!userEntity.getPassword().equals(passwordEncoder.encode(oldPassword))) {
            throw new UserBadRequestException("Wrong old password");
        }
        userEntity.setPassword(passwordEncoder.encode(newPassword));

        UserEntity updatedUser;

        try {
            updatedUser = userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException("Error during changing password");
        }

        return convertUserEntityToDTO(updatedUser);
    }

    @Override
    public void forgotPassword(String email) throws UserApiException, UserBadRequestException {
        UserEntity userEntity;

        userEntity = findByEmail(email);

        if (userEntity == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_EMAIL_MSG);
        }

        String token = generateResetToken();

        userEntity.setResetToken(token);

        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException(USER_GET_MSG);
        }

        //emailSender.sendEmail(userEntity.getEmail(), "Reset token", "Token for changing password is " + token);

    }

    @Override
    public boolean setPassword(String email, String resetToken, String newPassword, String confirmPassword) throws UserApiException, UserBadRequestException {
        if (newPassword != null) {
            UserValidator.passwordValidator(newPassword);
            if (!newPassword.equals(confirmPassword)) {
                throw new UserBadRequestException("Password dont match");
            }
        }

        UserEntity userEntity = findByEmail(email);

        if (!userEntity.getResetToken().equals(resetToken)) {
            throw new UserBadRequestException("Wrong reset token");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));

        userEntity.setResetToken(null);

        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException(USER_GET_MSG);
        }
        return true;
    }

    @Override
    public void delete(Integer id) throws UserApiException, UserBadRequestException {
        Optional<UserEntity> user;

        if (id == null) {
            throw new UserBadRequestException("User id can not be null or empty");
        }
        try {
            user = userRepository.findById(id);
        } catch (Exception e) {
            throw new UserApiException(USER_DELETE_MSG);
        }
        if (user.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_ID_MSG);
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserApiException(USER_DELETE_MSG);
        }
    }

    @Override
    public void sendVerificationCode(String email) throws UserApiException, UserBadRequestException {

        UserEntity user = findByEmail(email);

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserBadRequestException(USER_ALREADY_ACTIVATED_MSG);
        }

        user.setVerifyCode(generateVerifyCode());

        //send user email verification code

    }

    private UserEntity saveUser(UserEntity userEntity, String errorMessage) throws UserApiException {

        UserEntity user;

        try {
            user = userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException(errorMessage);
        }
        return user;
    }

    private UserEntity buildUserEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .year(userDTO.getYear())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .status(UserStatus.INACTIVE)
                .role(userDTO.getRole())
                .build();
    }
}