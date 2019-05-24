package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;

public class ResourceListAvailableEvent{

    private ResourceTaskResult result;

    public ResourceListAvailableEvent(ResourceTaskResult result){
        this.result = result;
    }

    public ResourceTaskResult getResult(){
        return result;
    }
}
