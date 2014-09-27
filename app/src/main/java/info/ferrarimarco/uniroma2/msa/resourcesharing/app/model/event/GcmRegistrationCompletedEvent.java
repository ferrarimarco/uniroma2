package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class GcmRegistrationCompletedEvent{

    private TaskResultType taskResultType;
    private Exception failureCause;

    public GcmRegistrationCompletedEvent(TaskResultType taskResultType){
        this.taskResultType = taskResultType;
    }

    public TaskResultType getTaskResultType(){
        return taskResultType;
    }

    public Exception getFailureCause(){
        return failureCause;
    }

    public void setFailureCause(Exception failureCause){
        this.failureCause = failureCause;
    }
}
