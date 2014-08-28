package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

public class AbstractTaskResult {

    protected String message;
    protected TaskResult taskResult;
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

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public Integer getCompletedTaskId() {
        return completedTaskId;
    }

    public void setCompletedTaskId(Integer completedTaskId) {
        this.completedTaskId = completedTaskId;
    }
}
