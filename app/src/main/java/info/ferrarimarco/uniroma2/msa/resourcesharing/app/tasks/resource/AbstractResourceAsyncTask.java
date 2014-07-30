package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.AbstractAsyncTask;

/**
 * Created by Ferrarim on 30/07/2014.
 */
public abstract class AbstractResourceAsyncTask extends AbstractAsyncTask<ResourceTaskResult> {

    @Inject
    GenericDao<Resource> resourceDao;

}
