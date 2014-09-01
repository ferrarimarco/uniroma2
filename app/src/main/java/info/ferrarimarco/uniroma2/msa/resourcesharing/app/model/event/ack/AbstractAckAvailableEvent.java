package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public abstract class AbstractAckAvailableEvent {

    private TaskResultType result;

    public AbstractAckAvailableEvent(TaskResultType result) {
        this.result = result;
    }

    public TaskResultType getResult() {
        return result;
    }
}
