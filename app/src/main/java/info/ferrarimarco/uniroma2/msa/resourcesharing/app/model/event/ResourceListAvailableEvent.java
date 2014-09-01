package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;

public class ResourceListAvailableEvent extends AbstractResourceEvent {
    public ResourceListAvailableEvent(ResourceTaskResult result) {
        super(result);
    }
}
