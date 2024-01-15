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

    // TODO:
    // private final TaskEmailSender taskEmailSender;

    /**
     * Creates a new user based on the provided UserDTO.
     *
     * @param userDTO UserDTO containing user information.
     * @return Created UserEntity.
     * @throws UserApiException if there is an error during the user creation process.
     */
    @Override
    public UserEntity createUser(UserDTO userDTO) throws UserApiException {
        UserEntity userByEmail;

        // Check if the user with the provided email already exists
        try {
            userByEmail = userRepository.findByEmail(userDTO.getEmail());
        } catch (Exception e) {
            throw new UserApiException(ERROR_CREATING_USER);
        }

        if (userByEmail != null) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        // Build UserEntity and set verification code
        UserEntity user = buildUserEntity(userDTO);
        user.setVerifyCode(generateVerifyCode());

        // TODO: Uncomment and use if email sending is implemented
        // taskEmailSender.sendEmail(userDTO.getEmail(), "asd", "asd");

        // Save the user and return
        return saveUser(user, ERROR_CREATING_USER);
    }


    /**
     * Verifies a user based on the provided verification code and email.
     *
     * @param verifyCode Verification code to validate.
     * @param email      Email of the user to be verified.
     * @return True if the user is successfully verified, otherwise false.
     * @throws UserApiException       if there is an error during the verification process.
     * @throws UserBadRequestException if the verification code or email is invalid.
     */
    @Override
    public boolean verifyUser(String verifyCode, String email) throws UserApiException, UserBadRequestException {
        // Validate the verification code length
        if (verifyCode == null || verifyCode.length() < 4) {
            throw new UserBadRequestException(VERIFICATION_CODE_LENGTH);
        }

        // Find the user by email
        UserEntity user = findByEmail(email);

        // Check if the user is already active
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserBadRequestException(USER_ALREADY_ACTIVATED);
        }

        // Check if the user has a verification code
        if (user.getVerifyCode() == null) {
            throw new UserBadRequestException(USER_DOES_NOT_HAVE_VERIFICATION_CODE);
        }

        // Check if the provided verification code matches the user's verification code
        if (!user.getVerifyCode().equals(verifyCode)) {
            throw new UserBadRequestException(INCORRECT_VERIFICATION_CODE);
        }

        // Set user status to ACTIVE, clear verification code, and save the user
        user.setStatus(UserStatus.ACTIVE);
        user.setVerifyCode(null);
        saveUser(user, ERROR_VERIFYING_USER);
        return true;
    }


    /**
     * Finds a user by their email.
     *
     * @param email Email of the user to be retrieved.
     * @return UserEntity corresponding to the provided email.
     * @throws UserApiException       if there is an error during the retrieval process.
     * @throws UserBadRequestException if the email is null or empty.
     */
    @Override
    public UserEntity findByEmail(String email) throws UserApiException, UserBadRequestException {
        UserEntity userEntity;

        // Validate email
        if (email == null || email.isBlank()) {
            throw new UserBadRequestException(USER_EMAIL_NULL_OR_EMPTY);
        }

        // Find user by email
        try {
            userEntity = userRepository.findByEmail(email);
        } catch (Exception e) {
            throw new UserApiException(ERROR_GETTING_USER);
        }

        // Check if user is not found
        if (userEntity == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_EMAIL + email);
        }
        return userEntity;
    }


    /**
     * Finds a user by their ID.
     *
     * @param id ID of the user to be retrieved.
     * @return UserEntity corresponding to the provided ID.
     * @throws UserApiException if there is an error during the retrieval process.
     */
    @Override
    public UserEntity findById(Integer id) throws UserApiException {
        Optional<UserEntity> userEntity;

        // Find user by ID
        try {
            userEntity = userRepository.findById(id);
        } catch (Exception e) {
            throw new UserApiException(ERROR_GETTING_USER);
        }

        // Check if user is not found
        if (userEntity.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_ID);
        }
        return userEntity.get();
    }


    /**
     * Retrieves a list of users based on optional search criteria.
     *
     * @param name    User's name for filtering.
     * @param surname User's surname for filtering.
     * @param role    User's role for filtering.
     * @return List of UserResponseDTOs representing the filtered users.
     * @throws UserApiException if there is an error during the retrieval process.
     */
    @Override
    @Secured({"PROJECT_ADMIN"})
    public List<UserResponseDTO> getAll(String name, String surname, String role) throws UserApiException {
        List<UserEntity> userEntities;

        UserRole userRole = null;

        // Convert role string to UserRole enum
        if (role != null) {
            userRole = Enum.valueOf(UserRole.class, role);
        }

        // Retrieve users based on optional search criteria
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
            throw new UserApiException(ERROR_GETTING_USERS);
        }

        // Convert UserEntities to UserResponseDTOs and return the list
        return convertUserEntitiesToDTOS(userEntities);
    }


    /**
     * Updates the information of a user based on the provided ID and UserDTO.
     *
     * @param id      ID of the user to be updated.
     * @param userDTO UserDTO containing updated user information.
     * @return Updated UserResponseDTO.
     * @throws UserApiException       if there is an error during the update process.
     * @throws UserBadRequestException if the ID is null, or if password, email, or role is provided in the UserDTO.
     */
    @Override
    public UserResponseDTO updateUser(Integer id, UserDTO userDTO) throws UserApiException, UserBadRequestException {
        // Validate user ID
        if (id == null) {
            throw new UserBadRequestException(USER_ID_NULL);
        }

        // Check if invalid fields are provided in UserDTO
        if (userDTO.getPassword() != null) {
            throw new UserBadRequestException(USER_PASSWORD_NULL_OR_MISSING);
        }
        if (userDTO.getEmail() != null) {
            throw new UserBadRequestException(USER_EMAIL_NULL_OR_MISSING);
        }
        if (userDTO.getRole() != null) {
            throw new UserBadRequestException(USER_ROLE_NULL_OR_MISSING);
        }

        // Validate user's year using custom validator
        UserValidator.yearValidator(userDTO.getYear());

        Optional<UserEntity> userEntity;
        UserEntity updatedUser;

        // Find user by ID
        try {
            userEntity = userRepository.findById(id);
        } catch (Exception e) {
            throw new UserApiException(ERROR_UPDATING_USER);
        }

        // Check if user is not found
        if (userEntity.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_ID);
        }

        // Update user information
        UserEntity user = userEntity.get();
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setYear(userDTO.getYear());

        // Save the updated user and return the UserResponseDTO
        try {
            updatedUser = userRepository.save(user);
        } catch (Exception e) {
            throw new UserApiException(ERROR_UPDATING_USER);
        }

        return convertUserEntityToDTO(updatedUser);
    }


    /**
     * Updates the password of a user.
     *
     * @param email          Email of the user whose password needs to be updated.
     * @param oldPassword    Old password for verification.
     * @param newPassword    New password to be set.
     * @param confirmPassword Confirmation of the new password.
     * @return Updated UserResponseDTO.
     * @throws UserApiException       if there is an error during the password update process.
     * @throws UserBadRequestException if password confirmation fails or the old password is incorrect.
     */
    @Override
    public UserResponseDTO updatePassword(String email, String oldPassword, String newPassword, String confirmPassword)
            throws UserApiException, UserBadRequestException {
        // Validate password confirmation
        if (!StringUtils.isEmpty(newPassword) && !newPassword.equals(confirmPassword)) {
            throw new UserBadRequestException(PASSWORDS_DONT_MATCH);
        }

        UserEntity userEntity;

        // Find user by email
        try {
            userEntity = findByEmail(email);
        } catch (Exception e) {
            throw new UserApiException(ERROR_CHANGING_PASSWORD_USER);
        }

        // Check if the old password matches the user's current password
        if (!userEntity.getPassword().equals(passwordEncoder.encode(oldPassword))) {
            throw new UserBadRequestException(WRONG_OLD_PASSWORD);
        }

        // Set the new password and save the updated user
        userEntity.setPassword(passwordEncoder.encode(newPassword));

        UserEntity updatedUser;

        try {
            updatedUser = userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException(ERROR_CHANGING_PASSWORD_USER);
        }

        // Convert the updated user to UserResponseDTO and return
        return convertUserEntityToDTO(updatedUser);
    }


    /**
     * Initiates the password reset process by generating and sending a reset token to the user's email.
     *
     * @param email Email of the user requesting a password reset.
     * @throws UserApiException       if there is an error during the password reset process.
     * @throws UserBadRequestException if the user with the provided email is not found.
     */
    @Override
    public void forgotPassword(String email) throws UserApiException, UserBadRequestException {
        UserEntity userEntity;

        // Find user by email
        userEntity = findByEmail(email);

        // Check if the user is not found
        if (userEntity == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_EMAIL);
        }

        // Generate a reset token and set it for the user
        String token = generateResetToken();
        userEntity.setResetToken(token);

        // Save the user with the reset token
        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException(ERROR_CHANGING_PASSWORD_USER);
        }

        // TODO: Uncomment and use if email sending is implemented
        // emailSender.sendEmail(userEntity.getEmail(), "Reset token", "Token for changing password is " + token);
    }


    /**
     * Verifies the user's identity using a reset token and updates the user's password.
     *
     * @param email           User's email.
     * @param resetToken      Token for password reset.
     * @param newPassword     New password.
     * @param confirmPassword Confirmation of the new password.
     * @return true if the password is successfully updated.
     * @throws UserApiException       if there is an error during the password update process.
     * @throws UserBadRequestException if the provided data is invalid.
     */
    @Override
    public boolean setPassword(String email, String resetToken, String newPassword, String confirmPassword)
            throws UserApiException, UserBadRequestException {
        if (newPassword != null) {
            // Validate the new password
            UserValidator.passwordValidator(newPassword);

            // Check if the passwords match
            if (!newPassword.equals(confirmPassword)) {
                throw new UserBadRequestException(PASSWORDS_DONT_MATCH);
            }
        }

        // Find the user by email
        UserEntity userEntity = findByEmail(email);

        // Check if the reset token matches
        if (!userEntity.getResetToken().equals(resetToken)) {
            throw new UserBadRequestException(RESET_TOKEN_MISMATCH);
        }

        // Update the password and reset token
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntity.setResetToken(null);

        try {
            // Save the updated user
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException(ERROR_GETTING_USER);
        }

        return true;
    }


    /**
     * Deletes a user by their ID.
     *
     * @param id User ID.
     * @throws UserApiException       if there is an error during the deletion process.
     * @throws UserBadRequestException if the provided ID is null or empty.
     */
    @Override
    public void delete(Integer id) throws UserApiException, UserBadRequestException {
        Optional<UserEntity> user;

        // Check if the provided ID is null or empty
        if (id == null) {
            throw new UserBadRequestException("User ID can not be null or empty");
        }

        try {
            // Find the user by ID
            user = userRepository.findById(id);
        } catch (Exception e) {
            throw new UserApiException(ERROR_DELETING_USER);
        }

        if (user.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_ID);
        }

        try {
            // Delete the user
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserApiException(ERROR_DELETING_USER);
        }
    }


    /**
     * Sends a verification code to the user's email for account activation.
     *
     * @param email User's email.
     * @throws UserApiException       if there is an error during the verification code sending process.
     * @throws UserBadRequestException if the provided email is invalid.
     */
    @Override
    public void sendVerificationCode(String email) throws UserApiException, UserBadRequestException {
        UserEntity user = findByEmail(email);

        // Check if the user is already activated
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserBadRequestException(USER_ALREADY_ACTIVATED);
        }

        // Generate and set a new verification code
        user.setVerifyCode(generateVerifyCode());

        // TODO: Uncomment and use if email sending is implemented
        // send user email verification code
    }


    /**
     * Saves a UserEntity to the repository.
     *
     * @param userEntity  UserEntity to be saved.
     * @param errorMessage Error message to be used in case of an exception.
     * @return Saved UserEntity.
     * @throws UserApiException if there is an error during the user save process.
     */
    private UserEntity saveUser(UserEntity userEntity, String errorMessage) throws UserApiException {
        try {
            // Save the user to the repository
            return userRepository.save(userEntity);
        } catch (Exception e) {
            throw new UserApiException(errorMessage);
        }
    }


    /**
     * Builds a UserEntity based on the provided UserDTO.
     *
     * @param userDTO UserDTO containing user information.
     * @return Built UserEntity.
     */
    private UserEntity buildUserEntity(UserDTO userDTO) {
        // Build and return a new UserEntity
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