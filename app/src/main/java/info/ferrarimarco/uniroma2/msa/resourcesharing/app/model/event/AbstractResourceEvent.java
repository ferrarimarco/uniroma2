package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;

public class AbstractResourceEvent {

    private ResourceTaskResult result;

    public AbstractResourceEvent(ResourceTaskResult result) {
        this.result = result;
    }

    public ResourceTaskResult getResult() {
        return result;
    }
}
