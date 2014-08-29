package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;

public class UserTaskResult extends AbstractTaskResult {

    private UserTaskType taskType;

    private User resultUser;

    public UserTaskResult(Integer completedTaskId) {
        super(completedTaskId);
    }

    public UserTaskResult(UserTaskType taskType, Integer completedTaskId) {
        this(completedTaskId);
        this.taskType = taskType;
    }

    public UserTaskResult(UserTaskType taskType, TaskResultType taskResultType, Integer completedTaskId) {
        this(taskType, completedTaskId);
        this.taskResultType = taskResultType;
    }

    public UserTaskResult(UserTaskType taskType, TaskResultType taskResultType, Integer completedTaskId, String message) {
        this(taskType, taskResultType, completedTaskId);
        this.message = message;
    }

    public UserTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(UserTaskType taskType) {
        this.taskType = taskType;
    }

    public User getResultUser() {
        return resultUser;
    }

    public void setResultUser(User resultUser) {
        this.resultUser = resultUser;
    }
}
