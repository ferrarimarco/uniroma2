package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import java.util.ArrayList;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

public class ResourceTaskResult{

    private List<Resource> resources;

    public ResourceTaskResult(){
        super();
        this.resources = new ArrayList<>();
    }

    public List<Resource> getResources(){
        return resources;
    }

    public void addResource(Resource resource){
        resources.add(resource);
    }
}
