package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class UserIdCheckAckAvailableEvent extends AbstractAckAvailableEvent {
    public UserIdCheckAckAvailableEvent(TaskResultType result) {
        super(result);
    }
}
