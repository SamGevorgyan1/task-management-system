package com.taskmanagement.dto.requestdto;

import com.taskmanagement.enums.UserRole;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    public static final String NAME_NULL_MSG = "User name can not be null or empty";
    public static final String NAME_REGEX = "[A-Z][a-z]+";
    public static final String NAME_REGEX_MESSAGE = "Name must start with uppercase and contain min 3 characters";
    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 64;
    public static final String NAME_LENGTH_MSG = "Name must have between " + NAME_MIN_LENGTH + "and " + NAME_MAX_LENGTH + " characters";

    public static final String SURNAME_NULL_MSG = "User surname can not be null or empty";
    public static final String SURNAME_REGEX = "[A-Z][a-z]+";
    public static final String SURNAME_REGEX_MESSAGE = "Surname must start with uppercase and contain min 3 characters";
    public static final int SURNAME_MIN_LENGTH = 3;
    public static final int SURNAME_MAX_LENGTH = 64;
    public static final String SURNAME_LENGTH_MSG = "Surname must have between " + SURNAME_MIN_LENGTH + "and " + SURNAME_MAX_LENGTH + " characters";

    public static final String EMAIL_REGEX = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String EMAIL_MSG = "not correct email format";


    @NotEmpty(message = NAME_NULL_MSG)
    @Pattern(regexp = NAME_REGEX,message = NAME_REGEX_MESSAGE)
    @NotEmpty
    @Length(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = NAME_LENGTH_MSG)
    private String name;

    @NotEmpty(message = SURNAME_NULL_MSG)
    @Pattern(regexp = SURNAME_REGEX,message = SURNAME_REGEX_MESSAGE)
    @NotEmpty
    @Length(min = SURNAME_MIN_LENGTH, max = SURNAME_MAX_LENGTH, message = SURNAME_LENGTH_MSG)
    private String surname;


    private Integer year;


    //@Pattern(regexp = EMAIL_REGEX,message = EMAIL_MSG)
    private String email;


    private String password;
    private UserRole role;
}
