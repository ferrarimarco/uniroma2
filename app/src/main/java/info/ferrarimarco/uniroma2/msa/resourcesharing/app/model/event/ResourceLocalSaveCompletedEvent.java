package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;

public class ResourceLocalSaveCompletedEvent extends AbstractResourceEvent {
    public ResourceLocalSaveCompletedEvent(ResourceTaskResult result) {
        super(result);
    }
}
