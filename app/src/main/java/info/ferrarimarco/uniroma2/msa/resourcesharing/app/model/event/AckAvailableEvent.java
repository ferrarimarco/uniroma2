package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class AckAvailableEvent {

    private TaskResultType result;

    public AckAvailableEvent(TaskResultType result) {
        this.result = result;
    }

    public TaskResultType getResult() {
        return result;
    }
}
