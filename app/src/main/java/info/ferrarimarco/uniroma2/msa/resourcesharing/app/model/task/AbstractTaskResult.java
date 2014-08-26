package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

public class AbstractTaskResult {

    protected String message;
    protected TaskResult taskResult;

    public AbstractTaskResult() {
        message = "";
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
}
