package com.taskmanagement.dto.responsedto;


import com.taskmanagement.enums.UserRole;
import com.taskmanagement.enums.UserStatus;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Integer id;
    private String name;
    private String surname;
    private Integer year;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
