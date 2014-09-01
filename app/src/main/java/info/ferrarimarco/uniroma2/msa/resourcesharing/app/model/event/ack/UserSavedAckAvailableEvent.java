package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class UserSavedAckAvailableEvent extends AbstractAckAvailableEvent {

    public UserSavedAckAvailableEvent(TaskResultType result) {
        super(result);
    }
}
