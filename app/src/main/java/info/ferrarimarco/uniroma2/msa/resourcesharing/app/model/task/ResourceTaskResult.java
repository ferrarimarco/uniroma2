package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import java.util.ArrayList;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

public class ResourceTaskResult extends AbstractTaskResult {
    private ResourceTaskType taskType;

    private List<Resource> resources;

    public ResourceTaskResult(Integer completedTaskId) {
        super(completedTaskId);
        this.resources = new ArrayList<>();
    }

    public ResourceTaskResult(ResourceTaskType taskType, Integer completedTaskId) {
        this(completedTaskId);
        this.taskType = taskType;
    }

    public ResourceTaskResult(ResourceTaskType taskType, Integer completedTaskId, TaskResultType taskResultType) {
        this(taskType, completedTaskId);
        this.taskResultType = taskResultType;
    }

    public ResourceTaskResult(ResourceTaskType taskType, Integer completedTaskId, TaskResultType taskResultType, List<Resource> resources) {
        this(taskType, completedTaskId, taskResultType);
        this.resources = resources;
    }

    public ResourceTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(ResourceTaskType taskType) {
        this.taskType = taskType;
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
}
