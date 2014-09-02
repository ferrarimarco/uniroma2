package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class ResourceSavedAckAvailableEvent extends AbstractAckAvailableEvent {
    private Long androidId;

    public ResourceSavedAckAvailableEvent(TaskResultType result, Long androidId) {
        super(result);
        this.androidId = androidId;
    }

    public Long getAndroidId() {
        return androidId;
    }
}
