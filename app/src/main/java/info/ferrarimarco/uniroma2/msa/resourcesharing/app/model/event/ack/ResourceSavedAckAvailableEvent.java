package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class ResourceSavedAckAvailableEvent extends AbstractAckAvailableEvent {
    private Resource resource;

    public ResourceSavedAckAvailableEvent(TaskResultType result, Resource resource) {
        super(result);
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }
}
