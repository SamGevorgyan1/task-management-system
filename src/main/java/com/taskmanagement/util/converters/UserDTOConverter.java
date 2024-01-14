package com.taskmanagement.util.converters;

import com.taskmanagement.dto.responsedto.UserResponseDTO;
import com.taskmanagement.model.UserEntity;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTOConverter {


    public static UserResponseDTO convertUserEntityToDTO(UserEntity userEntity) {
        return UserResponseDTO.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .surname(userEntity.getSurname())
                .year(userEntity.getYear())
                .status(userEntity.getStatus())
                .role(userEntity.getRole())
                .email(userEntity.getEmail())
                .build();
    }


    public static List<UserResponseDTO> convertUserEntitiesToDTOS(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(UserDTOConverter::convertUserEntityToDTO)
                .collect(Collectors.toList());
    }
}