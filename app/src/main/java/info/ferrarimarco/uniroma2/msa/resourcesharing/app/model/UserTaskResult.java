package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

public class UserTaskResult {

    public enum UserTaskType {
        USER_LOGIN,
        REGISTER_NEW_USER;
    }

    public enum UserTaskResultType {
        SUCCESS,
        FAILURE;
    }

    private UserTaskType taskType;
    private UserTaskResultType userTaskResultType;

    private String details;

    public UserTaskResult(UserTaskType taskType, UserTaskResultType userTaskResultType) {
        this.taskType = taskType;
        this.userTaskResultType = userTaskResultType;
        this.details = "";
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
}
