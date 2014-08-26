package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import java.util.ArrayList;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

public class ResourceTaskResult extends AbstractTaskResult {
    private ResourceTaskType taskType;

    private List<Resource> resources;

    public ResourceTaskResult() {
        super();
        this.resources = new ArrayList<>();
    }

    public ResourceTaskResult(ResourceTaskType taskType) {
        this();
        this.taskType = taskType;
    }

    public ResourceTaskResult(ResourceTaskType taskType, TaskResult taskResult) {
        this(taskType);
        this.taskResult = taskResult;

    }

    public ResourceTaskResult(ResourceTaskType taskType, TaskResult taskResult, List<Resource> resources) {
        this(taskType, taskResult);
        this.resources = resources;
    }

    public ResourceTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(ResourceTaskType taskType) {
        this.taskType = taskType;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
