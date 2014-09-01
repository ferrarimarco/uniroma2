package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.RegisteredUserAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.UserDeletionCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.UserLocalRegistrationCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl.HashingServiceImpl;

public class UserService extends AbstractPersistenceService {

    private static User currentUser;

    @Inject
    GenericDao<User> userDao;

    @Inject
    HashingServiceImpl hashingService;

    @Inject
    public UserService(Context context) {
        super(context);
    }

    public void readRegisteredUserAsync() {
        if (currentUser == null) {
            new AsyncTask<Void, Void, User>() {
                @Override
                protected User doInBackground(Void... params) {
                    return UserService.this.readRegisteredUserSync();
                }

                @Override
                protected void onPostExecute(User user) {
                    bus.post(new RegisteredUserAvailableEvent(user));
                }
            }.execute();
        } else {
            bus.post(new RegisteredUserAvailableEvent(currentUser));
        }
    }

    public User readRegisteredUserSync() {
        if (currentUser == null) {
            try {
                userDao.open(User.class);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }

            User registeredUser = new User();
            String registeredUserId = context.getResources().getString(R.string.registered_user_id);
            Long registered_user_id = Long.parseLong(registeredUserId);
            registeredUser.setId(registered_user_id);

            List<User> users;

            try {
                users = userDao.read(registeredUser);
                userDao.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }

            // Check if there is a registered user
            if (users != null && !users.isEmpty()) {
                registeredUser = users.get(0);
            }

            currentUser = registeredUser;
        }

        return currentUser;
    }

    public void registerNewUser(String userId, String password) {
        new AsyncTask<String, Void, UserTaskResult>() {
            @Override
            protected UserTaskResult doInBackground(String... params) {
                // Register the user in local DB
                String userId = params[0];
                String password = params[1];

                try {
                    userDao.open(User.class);
                } catch (SQLException e) {
                    return new UserTaskResult(UserTaskType.REGISTER_NEW_USER, TaskResultType.FAILURE, e.getMessage());
                }

                String hashedPassword = new String(Hex.encodeHex(hashingService.hash(password)));
                User user = new User(userId, userId, hashedPassword, DateTime.now());

                Long registered_user_id = Long.parseLong(context.getResources().getString(R.string.registered_user_id));
                user.setId(registered_user_id);

                try {
                    userDao.save(user);
                    userDao.close();
                } catch (SQLException e) {
                    return new UserTaskResult(UserTaskType.REGISTER_NEW_USER, TaskResultType.FAILURE, e.getMessage());
                }

                return new UserTaskResult(UserTaskType.REGISTER_NEW_USER, TaskResultType.SUCCESS);
            }

            @Override
            protected void onPostExecute(UserTaskResult result) {
                bus.post(new UserLocalRegistrationCompletedEvent(result));
            }
        }.execute(userId, password);
    }

    public void deleteRegisteredUser() {
        new AsyncTask<Void, Void, UserTaskResult>() {
            @Override
            protected UserTaskResult doInBackground(Void... params) {
                try {
                    userDao.open(User.class);
                } catch (SQLException e) {
                    return new UserTaskResult(UserTaskType.DELETE_REGISTERED_USER, TaskResultType.FAILURE, e.getMessage());
                }
                User user = new User();
                Long registered_user_id = Long.parseLong(context.getResources().getString(R.string.registered_user_id));
                user.setId(registered_user_id);

                try {
                    userDao.delete(user);
                    userDao.close();
                } catch (SQLException e) {
                    return new UserTaskResult(UserTaskType.DELETE_REGISTERED_USER, TaskResultType.FAILURE, e.getMessage());
                }

                return new UserTaskResult(UserTaskType.DELETE_REGISTERED_USER, TaskResultType.SUCCESS);
            }

            @Override
            protected void onPostExecute(UserTaskResult result) {
                bus.post(new UserDeletionCompletedEvent(result));
            }
        }.execute();
    }
}
