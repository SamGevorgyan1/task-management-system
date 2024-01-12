package com.taskmanagement.service;

import com.taskmanagement.common.utils.MD5Encoder;
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
import com.taskmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private MD5Encoder passwordEncoder;

    UserDTO userDTO = UserDTO.builder()
            .name("Ani")
            .surname("Avagyan")
            .year(2002)
            .email("ani@gmail.com")
            .password("123456Aa")
            .role(UserRole.ADMIN)
            .build();


    UserEntity userEntity = UserEntity.builder()
            .id(15)
            .name("Ani")
            .surname("Avagyan")
            .year(2002)
            .email("ani@gmail.com")
            .password("123456Aa")
            .role(UserRole.USER)
            .build();


    @Test
    void testSave() throws UserApiException {
        given(passwordEncoder.encode(any(String.class))).willReturn("any(String.class)");
        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);

        //  doNothing().when(emailSender).sendEmail(any(),any(),any());

        UserEntity user = userService.createUser(userDTO);

        assertEquals(user.getName(), userEntity.getName());
        assertEquals(user.getSurname(), userEntity.getSurname());
        assertEquals(user.getYear(), userEntity.getYear());
        assertEquals(user.getEmail(), userEntity.getEmail());
        assertEquals(user.getStatus(), userEntity.getStatus());
        assertEquals(user.getRole(), userEntity.getRole());

        verify(userRepository, times(1)).save(any());

        given(userRepository.save(any(UserEntity.class))).willThrow(RuntimeException.class);
        assertThrows(UserApiException.class, () -> userService.createUser(userDTO));


        given(userRepository.findByEmail(any(String.class))).willReturn(userEntity);
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDTO));

    }

    @Test
    void testGetByUserName() throws UserApiException, UserBadRequestException {
        given(userRepository.findByEmail(any(String.class))).willReturn(userEntity);

        UserEntity user = userService.findByEmail(userEntity.getEmail());

        assertEquals(user.getName(), userEntity.getName());
        assertEquals(user.getSurname(), userEntity.getSurname());
        assertEquals(user.getYear(), userEntity.getYear());
        assertEquals(user.getEmail(), userEntity.getEmail());
        assertEquals(user.getStatus(), userEntity.getStatus());
        assertEquals(user.getRole(), userEntity.getRole());

        given(userRepository.findByEmail(any(String.class))).willThrow(RuntimeException.class);
        assertThrows(UserApiException.class, () -> userService.findByEmail(userEntity.getEmail()));

        given(userRepository.findByEmail(any(String.class))).willReturn((any(UserEntity.class)));
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(userEntity.getEmail()));

        assertThrows(UserBadRequestException.class, () -> userService.findByEmail(null));

        assertThrows(UserBadRequestException.class, () -> userService.findByEmail(" "));

        assertThrows(UserBadRequestException.class, () -> userService.findByEmail(""));
    }


    @Test
    void testUpdateUser() throws UserApiException, UserBadRequestException {

        int userId = 1;
        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));

        UserDTO updatedUserDTO = UserDTO.builder()
                .name("Mary")
                .surname("Avagyan")
                .year(2001)
                .build();


        UserResponseDTO updatedUser = userService.updateUser(userId, updatedUserDTO);
        assertEquals(updatedUserDTO.getName(), updatedUser.getName());
        assertEquals(updatedUserDTO.getSurname(), updatedUser.getSurname());
        assertEquals(updatedUserDTO.getYear(), updatedUser.getYear());


        Assertions.assertThrows(UserBadRequestException.class, () -> userService.updateUser(null, updatedUserDTO));

        given(userRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(1, updatedUserDTO));

        given(userRepository.findById(any(Integer.class))).willThrow(new RuntimeException("Error during getting user"));
        Assertions.assertThrows(UserApiException.class, () -> userService.updateUser(userId, updatedUserDTO));

        Assertions.assertThrows(UserBadRequestException.class, () -> {
            updatedUserDTO.setRole(UserRole.USER);
            userService.updateUser(1, updatedUserDTO);
        });

        Assertions.assertThrows(UserBadRequestException.class, () -> {
            updatedUserDTO.setEmail("email");
            userService.updateUser(1, updatedUserDTO);
        });

        Assertions.assertThrows(UserBadRequestException.class, () -> {
            updatedUserDTO.setPassword("password");
            userService.updateUser(1, updatedUserDTO);
        });

        Assertions.assertThrows(UserBadRequestException.class, () -> {
            updatedUserDTO.setYear(12);
            userService.updateUser(1, updatedUserDTO);
        });

        Assertions.assertThrows(UserBadRequestException.class, () -> {
            updatedUserDTO.setYear(12222);
            userService.updateUser(1, updatedUserDTO);
        });

    }

    @Test
    void testForgotPassword() throws UserApiException, UserBadRequestException {

        given(userRepository.findByEmail(userEntity.getEmail())).willReturn(userEntity);


        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);


        userService.forgotPassword(userEntity.getEmail());

        given(userRepository.findByEmail(any(String.class))).willThrow(RuntimeException.class);
        Assertions.assertThrows(UserApiException.class, () -> userService.forgotPassword(userEntity.getEmail()));


        given(userRepository.findByEmail(any(String.class))).willReturn(null);
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.forgotPassword(userEntity.getEmail()));
    }


    @Test
    void testDeleteUser() throws UserApiException, UserBadRequestException {
        given(userRepository.findById(any(Integer.class))).willReturn(Optional.of(userEntity));

        doNothing().when(userRepository).deleteById(any(Integer.class));

        userService.delete(1);

        Mockito.verify(userRepository).deleteById(1);

        Assertions.assertThrows(UserBadRequestException.class, () -> userService.delete(null));

        given(userRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.delete(1));

        Assertions.assertThrows(UserBadRequestException.class, () -> userService.delete(null));
    }

    @Test
    void testGetAllUsers() throws UserApiException {

        List<UserEntity> users = List.of(
                new UserEntity(1, "Samvel", "Gevorgyan", 2003, "samvel@gmail.com", "password", UserRole.PROJECT_ADMIN, UserStatus.ACTIVE),
                new UserEntity(2, "Samvel", "Gevorgyan", 2002, "samvel2@gmail.com", "password", UserRole.PROJECT_ADMIN, UserStatus.ACTIVE)
        );
        given(userRepository.findAll()).willReturn(users);


        List<UserResponseDTO> allUsers = userService.getAll(null, null, null);

        assertUsersEqual(allUsers, users);

        given(userRepository.getByName("Samvel")).willReturn(users);
        List<UserResponseDTO> filteredUsersByName = userService.getAll("Samvel", null, null);


        assertUsersEqual(filteredUsersByName, users);

        given(userRepository.getByName("Gevorgyan")).willReturn(users);
        List<UserResponseDTO> filteredUsersBySurname = userService.getAll("Gevorgyan", null, null);

        assertUsersEqual(filteredUsersBySurname, users);

        //given(userRepository.findAll()).willThrow(new RuntimeException("Repository error"));

    }

    private void assertUsersEqual(List<UserResponseDTO> userResponseDTOs, List<UserEntity> userEntities) {
        //assertThat(userResponseDTOs).hasSize(userEntities.size());

        for (int i = 0; i < userEntities.size(); i++) {
            UserEntity userEntity = userEntities.get(i);
            UserResponseDTO userResponseDTO = userResponseDTOs.get(i);

            // assertEquals(userEntity.getId(), userResponseDTO.getId());
            assertEquals(userEntity.getName(), userResponseDTO.getName());
            assertEquals(userEntity.getSurname(), userResponseDTO.getSurname());
            assertEquals(userEntity.getYear(), userResponseDTO.getYear());
            assertEquals(userEntity.getEmail(), userResponseDTO.getEmail());
            assertEquals(userEntity.getStatus(), userResponseDTO.getStatus());
            assertEquals(userEntity.getRole(), userResponseDTO.getRole());
        }
    }


}
