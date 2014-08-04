package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;

public class UserTaskResult {

    private UserTaskType taskType;
    private TaskResultType taskResultType;

    private String details;

    private User resultUser;

    private Boolean registeredUserPresent;

    public UserTaskResult() {
        this.details = "";
        this.registeredUserPresent = Boolean.FALSE;
    }

    public UserTaskResult(UserTaskType taskType) {
        this();
        this.taskType = taskType;
    }

    public UserTaskResult(UserTaskType taskType, TaskResultType taskResultType) {
        this(taskType);
        this.taskResultType = taskResultType;

    }

    public UserTaskResult(UserTaskType taskType, TaskResultType taskResultType, String details) {
        this(taskType, taskResultType);
        this.details = details;
    }

    public UserTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(UserTaskType taskType) {
        this.taskType = taskType;
    }

    public TaskResultType getTaskResultType() {
        return taskResultType;
    }

    public void setTaskResultType(TaskResultType userTaskResultType) {
        this.taskResultType = userTaskResultType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean isRegisteredUserPresent() {
        return registeredUserPresent;
    }

    public void setRegisteredUserPresent(Boolean registeredUserPresent) {
        this.registeredUserPresent = registeredUserPresent;
    }

    public User getResultUser() {
        return resultUser;
    }

    public void setResultUser(User resultUser) {
        this.resultUser = resultUser;
    }
}
