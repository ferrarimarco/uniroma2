package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;

public class ResourceSaveCompletedEvent extends AbstractResourceEvent {
    public ResourceSaveCompletedEvent(ResourceTaskResult result) {
        super(result);
    }
}
