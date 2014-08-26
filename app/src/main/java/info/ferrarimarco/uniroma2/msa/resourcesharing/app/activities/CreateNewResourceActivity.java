package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDao;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource.SaveResourceAsyncTask;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisteredUserCheckAsyncTask;

public class CreateNewResourceActivity extends AbstractAsyncTaskActivity {

    @Inject
    GenericDao<Resource> resourceDao;

    private SaveResourceAsyncTask saveResourceAsyncTask;

    @InjectView(R.id.resourceTitleEditText)
    EditText titleEditText;

    @InjectView(R.id.resourceDescriptionEditText)
    EditText descriptionEditText;

    @InjectView(R.id.resourceLocationEditText)
    EditText locationEditText;

    @InjectView(R.id.resourceAcquisitionModeEditText)
    EditText acquisitionModeEditText;

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

        this.defaultInitialization(mProgressView, mCreateNewResourceFormView);

        showProgress(true);

        registeredUserCheckTask = objectGraph.get(RegisteredUserCheckAsyncTask.class);
        registeredUserCheckTask.initTask(this, this.getApplicationContext());
        registeredUserCheckTask.execute(getResources().getString(R.string.registered_user_id));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_new_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showProgress(true);

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_new_resource) {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String acquisitionMode = acquisitionModeEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String currentUserName = currentUser.getName();

            saveResourceAsyncTask = objectGraph.get(SaveResourceAsyncTask.class);
            saveResourceAsyncTask.initTask(this, this.getApplicationContext());
            saveResourceAsyncTask.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {

        if (result instanceof UserTaskResult) {
            UserTaskResult taskResult = (UserTaskResult) result;

            if (taskResult.getTaskResult().equals(TaskResult.SUCCESS)) {
                if (taskResult.isRegisteredUserPresent()) {
                    currentUser = taskResult.getResultUser();
                }
            } else {
                // TODO: handle error condition
            }
        } else if (result instanceof ResourceTaskResult) {
            ResourceTaskResult taskResult = (ResourceTaskResult) result;

            if (taskResult.getTaskResult().equals(TaskResult.SUCCESS)) {
                // TODO: show a notification (toast)
                finish();
            } else {
                // TODO: handle error condition
            }
        }

        showProgress(false);
    }

    @Override
    public void onBackgroundTaskCancelled(Object cancelledTask) {
        if (cancelledTask instanceof RegisteredUserCheckAsyncTask) {
            registeredUserCheckTask = null;
        } else if (cancelledTask instanceof SaveResourceAsyncTask) {
            saveResourceAsyncTask = null;
        }
    }

}
