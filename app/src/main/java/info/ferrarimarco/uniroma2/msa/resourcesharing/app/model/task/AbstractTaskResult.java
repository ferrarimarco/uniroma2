package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

public class AbstractTaskResult {

    protected String message;
    protected TaskResultType taskResultType;

    public AbstractTaskResult() {
        message = "";
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
}
