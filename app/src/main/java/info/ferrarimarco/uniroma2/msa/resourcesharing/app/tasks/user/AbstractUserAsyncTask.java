package info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.HashingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.AbstractAsyncTask;

/**
 * Created by Marco on 28/07/2014.
 */
public abstract class AbstractUserAsyncTask extends AbstractAsyncTask<UserTaskResult> {

    @Inject
    GenericDao<User> userDao;

    @Inject
    HashingServiceImpl hashingService;
}
