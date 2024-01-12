package com.taskmanagement.util.messages;

public interface TaskErrorMessage {

    String TASK_NOT_FOUND_MSG = "Task not found with given id";

    String ASSIGNEE_NOT_FOUND = "Assignee not found with given email";

    String ERROR_CREATING_TASK = "Error during creating task";
    String ERROR_GETTING_TASKS = "Error during getting tasks";
    String ERROR_GETTING_TASK = "Error during getting task";
    String ERROR_UPDATING_TASK = "Error during updating task";
    String ERROR_DELETING_TASK = "Error during deleting task";

    String INVALID_TASK_STATUS = "Invalid task status. Allowed values are: PENDING, IN_PROGRESS, COMPLETED";
    String INVALID_TASK_PRIORITY = "Invalid task priority. Allowed values are: HIGH, MEDIUM, LOW";

}
