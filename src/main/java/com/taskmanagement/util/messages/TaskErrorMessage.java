package com.taskmanagement.util.messages;

public interface TaskErrorMessage {

    // Task ID Error
    String TASK_ID_NULL = "Task id cannot be null";

    // Task Not Found Error
    String TASK_NOT_FOUND = "Task not found with the given ID";

    // Assignee Not Found Error
    String ASSIGNEE_NOT_FOUND = "Assignee not found with the given email";

    // General Task Errors
    String ERROR_CREATING_TASK = "Error creating task";
    String ERROR_GETTING_TASKS = "Error getting tasks";
    String ERROR_GETTING_TASK = "Error getting task";
    String ERROR_UPDATING_TASK = "Error updating task";
    String ERROR_DELETING_TASK = "Error deleting task";

    // Task Status Error
    String INVALID_TASK_STATUS = "Invalid task status. Allowed values are: PENDING, IN_PROGRESS, COMPLETED";
    String TASK_STATUS_NULL_OR_EMPTY = "Task status cannot be null or empty";

    // Task Priority Error
    String INVALID_TASK_PRIORITY = "Invalid task priority. Allowed values are: HIGH, MEDIUM, LOW";
}
