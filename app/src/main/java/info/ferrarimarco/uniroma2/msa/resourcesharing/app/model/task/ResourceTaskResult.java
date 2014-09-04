package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import java.util.ArrayList;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

public class ResourceTaskResult extends AbstractTaskResult {
    private ResourceTaskType taskType;

    private List<Resource> resources;
    private Long androidId;

    public ResourceTaskResult() {
        super();
        this.resources = new ArrayList<>();
    }

    public ResourceTaskResult(ResourceTaskType taskType) {
        this();
        this.taskType = taskType;
    }

    public ResourceTaskResult(ResourceTaskType taskType, TaskResultType taskResultType) {
        this(taskType);
        this.taskResultType = taskResultType;
    }

    public ResourceTaskResult(ResourceTaskType taskType, TaskResultType taskResultType, Long androidId) {
        this(taskType, taskResultType);
        this.androidId = androidId;
    }

    public ResourceTaskType getTaskType() {
        return taskType;
    }

    public TaskResultType getTaskResultType() {
        return taskResultType;
    }

    public void setTaskResultType(TaskResultType taskResultType) {
        this.taskResultType = taskResultType;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public Long getAndroidId() {
        return androidId;
    }
}
