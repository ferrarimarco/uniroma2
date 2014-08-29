package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

public class AbstractTaskResult {

    protected String message;
    protected TaskResultType taskResultType;
    protected Integer completedTaskId;

    public AbstractTaskResult(Integer completedTaskId) {
        message = "";
        this.completedTaskId = completedTaskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TaskResultType getTaskResultType() {
        return taskResultType;
    }

    public void setTaskResultType(TaskResultType taskResultType) {
        this.taskResultType = taskResultType;
    }

    public Integer getCompletedTaskId() {
        return completedTaskId;
    }

    public void setCompletedTaskId(Integer completedTaskId) {
        this.completedTaskId = completedTaskId;
    }
}
