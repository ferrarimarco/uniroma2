package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;

public class UserTaskResult extends AbstractTaskResult {

    private UserTaskType taskType;

    private User resultUser;

    private Boolean registeredUserPresent;

    public UserTaskResult() {
        super();
        this.registeredUserPresent = Boolean.FALSE;
    }

    public UserTaskResult(UserTaskType taskType) {
        this();
        this.taskType = taskType;
    }

    public UserTaskResult(UserTaskType taskType, TaskResult taskResult) {
        this(taskType);
        this.taskResult = taskResult;
    }

    public UserTaskResult(UserTaskType taskType, TaskResult taskResult, String message) {
        this(taskType, taskResult);
        this.message = message;
    }

    public UserTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(UserTaskType taskType) {
        this.taskType = taskType;
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
