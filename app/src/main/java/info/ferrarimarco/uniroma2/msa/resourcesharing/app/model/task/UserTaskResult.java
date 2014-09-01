package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

public class UserTaskResult extends AbstractTaskResult {

    private UserTaskType taskType;

    public UserTaskResult() {
        super();
    }

    public UserTaskResult(UserTaskType taskType) {
        this();
        this.taskType = taskType;
    }

    public UserTaskResult(UserTaskType taskType, TaskResultType taskResultType) {
        this(taskType);
        this.taskResultType = taskResultType;
    }

    public UserTaskResult(UserTaskType taskType, TaskResultType taskResultType, String message) {
        this(taskType, taskResultType);
        this.message = message;
    }

    public UserTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(UserTaskType taskType) {
        this.taskType = taskType;
    }
}
