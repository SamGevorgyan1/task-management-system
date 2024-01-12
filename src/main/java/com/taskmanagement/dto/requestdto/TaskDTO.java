package com.taskmanagement.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    public static final String TITLE_NULL_MSG = "Task title can not be null or empty";
    public static final String TITLE_REGEX = "[A-Z][a-z]+";
    public static final String TITLE_REGEX_MESSAGE = "Title must start with uppercase and contain min 3 characters";
    public static final int TITLE_MIN_LENGTH = 3;
    public static final int TITLE_MAX_LENGTH = 64;
    public static final String TITLE_LENGTH_MSG = "Title must have between " + TITLE_MIN_LENGTH + "and " + TITLE_MAX_LENGTH + " characters";


    public static final String EMAIL_REGEX = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String EMAIL_MSG = "Invalid email format";
    public static final String PRIORITY_MSG = "Priority can not be null or empty";


    public static final String STATUS_NULL_MSG = "Task status can not be null";
    public static final String STATUS_REGEX = "PENDING|IN_PROGRESS|COMPLETED";
    public static final String STATUS_REGEX_MESSAGE = "Invalid task status. Allowed values are: PENDING, IN_PROGRESS, COMPLETED";

    public static final String PRIORITY_NULL_MSG = "Task priority can not be null";
    public static final String PRIORITY_REGEX = "HIGH|MEDIUM|LOW";
    public static final String PRIORITY_REGEX_MESSAGE = "Invalid task priority. Allowed values are: HIGH, MEDIUM, LOW";

    @NotEmpty(message = TITLE_NULL_MSG)
    @NotNull(message = TITLE_NULL_MSG)
    @Length(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH, message = TITLE_LENGTH_MSG)
    @Pattern(regexp = TITLE_REGEX, message = TITLE_REGEX_MESSAGE)
    private String title;

    private String description;

    @JsonProperty("status")
    @NotNull(message = STATUS_NULL_MSG)
    @Pattern(regexp = STATUS_REGEX, message = STATUS_REGEX_MESSAGE)
    private String taskStatus;

    @JsonProperty("priority")
    @NotNull(message = PRIORITY_NULL_MSG)
    @Pattern(regexp = PRIORITY_REGEX, message = PRIORITY_REGEX_MESSAGE)
    private String taskPriority;

    @NotNull(message = "Email can not be null")
    @Pattern(regexp = EMAIL_REGEX, message = EMAIL_MSG)
    private String assigneeEmail;

}