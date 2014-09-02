package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ResourceLocalSaveCompletedEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ack.ResourceSavedAckAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;

public class CreateNewResourceActivity extends AbstractAsyncTaskActivity {

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

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_resource);
        this.defaultInitialization(mProgressView, mCreateNewResourceFormView);

        objectGraph.inject(this);
        ButterKnife.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = userService.readRegisteredUserSync();
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
            String currentUserId = currentUser.getEmail();

            Resource resource = new Resource(title, description, location, DateTime.now(), acquisitionMode, currentUserId, Resource.ResourceType.CREATED_BY_ME, false);
            resourceService.saveResourceLocal(resource, false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void resourceSavedAckAvailableEvent(ResourceSavedAckAvailableEvent event) {
        finish();

        if (TaskResultType.RESOURCE_SAVED.equals(event.getResult())) {
            // TODO: show a notification (toast)

            // save the updated resource
            resourceService.saveResourceLocal(event.getResource(), true);
            finish();
        } else if (TaskResultType.RESOURCE_NOT_SAVED.equals(event.getResult())) {
            // TODO: handle this error condition
        }
    }

    @Subscribe
    public void resourceLocalSaveCompletedAvailable(ResourceLocalSaveCompletedEvent event) {
        ResourceTaskResult taskResult = event.getResult();

        if (TaskResultType.RESOURCE_SAVED.equals(taskResult.getTaskResultType())) {
            gcmMessagingService.sendNewResource(taskResult.getResources().get(0));
        } else {
            // TODO: handle this error condition
        }
    }
}
