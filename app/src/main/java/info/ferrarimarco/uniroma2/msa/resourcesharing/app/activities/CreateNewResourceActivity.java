package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.sql.SQLException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.ResourceType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisteredUserCheckAsyncTask;

public class CreateNewResourceActivity extends Activity implements AsyncCaller {

    private ObjectGraph objectGraph;

    @Inject
    GenericDao<Resource> resDao;

    @InjectView(R.id.resourceTitleEditText)
    EditText titleEditText;

    @InjectView(R.id.resourceDescriptionEditText)
    EditText descriptionEditText;

    @InjectView(R.id.resourceLocationEditText)
    EditText locationEditText;

    @InjectView(R.id.resourceCreationTimeEditText)
    EditText creationTimeEditText;

    @InjectView(R.id.create_new_resource_progress)
    View mProgressView;

    @InjectView(R.id.create_new_resource_form)
    View mCreateNewResourceFormView;

    private RegisteredUserCheckAsyncTask registeredUserCheckTask;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_resource);

        objectGraph = ObjectGraph.create(new ContextModuleImpl(this.getApplicationContext()), new DaoModuleImpl());
        objectGraph.inject(this);

        ButterKnife.inject(this);

        registeredUserCheckTask = objectGraph.get(RegisteredUserCheckAsyncTask.class);
        registeredUserCheckTask.initTask(this, this.getApplicationContext());
        registeredUserCheckTask.execute(getResources().getString(R.string.registered_user_id));

        showProgress(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_new_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_new_resource) {
            try {
                resDao.open(Resource.class);

                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String creationTime = creationTimeEditText.getText().toString();
                //String acquisitionMode = acquisitionModeEditText.getText().toString();
                String location = locationEditText.getText().toString();

                Resource newRes = new Resource(title, description, location, DateTime.now(), "ACQ", currentUser.getName(), ResourceType.CREATED_BY_ME);
                resDao.save(newRes);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                resDao.close();
            }

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {

        UserTaskResult taskResult = (UserTaskResult) result;

        if (taskResult.getTaskResultType().equals(TaskResultType.SUCCESS)) {
            if (taskResult.isRegisteredUserPresent()) {
                currentUser = taskResult.getResultUser();
            }
        } else {
            // TODO: handle error condition
        }

        showProgress(false);
    }

    @Override
    public void onBackgroundTaskCancelled() {
        registeredUserCheckTask = null;
    }

    /**
     * Shows the progress UI and hides the form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mCreateNewResourceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mCreateNewResourceFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCreateNewResourceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
