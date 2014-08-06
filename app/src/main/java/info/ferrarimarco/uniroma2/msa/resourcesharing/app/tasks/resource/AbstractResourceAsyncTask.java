package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource;

import java.sql.SQLException;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.AbstractAsyncTask;

public abstract class AbstractResourceAsyncTask extends AbstractAsyncTask<ResourceTaskResult> {

    @Inject
    GenericDao<Resource> resourceDao;

    protected ResourceTaskType taskType;

    protected ResourceTaskResult result;

    public AbstractResourceAsyncTask(){
        result = new ResourceTaskResult();

        try {
            resourceDao.open(Resource.class);
        } catch (SQLException e) {
            e.printStackTrace();
            result.setTaskResultType(TaskResultType.FAILURE);
        }
    }

    public ResourceTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(ResourceTaskType taskType) {
        this.taskType = taskType;
    }

}
