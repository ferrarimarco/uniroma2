package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

public class UserTaskResult {

    public enum UserTaskType {
        USER_LOGIN,
        REGISTER_NEW_USER,
        CHECK_REGISTERED_USER
    }

    public enum UserTaskResultType {
        SUCCESS,
        FAILURE
    }

    private UserTaskType taskType;
    private UserTaskResultType userTaskResultType;

    private String details;

    private Boolean registeredUserPresent;

    public UserTaskResult() {
        this.details = "";
        this.registeredUserPresent = Boolean.FALSE;
    }

    public UserTaskResult(UserTaskType taskType) {
        this();
        this.taskType = taskType;
    }

    public UserTaskResult(UserTaskType taskType, UserTaskResultType userTaskResultType) {
        this(taskType);
        this.userTaskResultType = userTaskResultType;

    }

    public UserTaskResult(UserTaskType taskType, UserTaskResultType userTaskResultType, String details) {
        this(taskType, userTaskResultType);
        this.details = details;
    }

    public UserTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(UserTaskType taskType) {
        this.taskType = taskType;
    }

    public UserTaskResultType getUserTaskResultType() {
        return userTaskResultType;
    }

    public void setUserTaskResultType(UserTaskResultType userTaskResultType) {
        this.userTaskResultType = userTaskResultType;
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
}
