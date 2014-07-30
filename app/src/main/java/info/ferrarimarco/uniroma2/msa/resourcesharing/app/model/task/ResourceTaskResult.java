package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task;

import java.util.ArrayList;
import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

/**
 * Created by Ferrarim on 30/07/2014.
 */
public class ResourceTaskResult {
    private ResourceTaskType taskType;
    private TaskResultType taskResultType;

    private List<Resource> resources;

    public ResourceTaskResult() {
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

    public ResourceTaskResult(ResourceTaskType taskType, TaskResultType taskResultType, List<Resource> resources) {
        this(taskType, taskResultType);
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
