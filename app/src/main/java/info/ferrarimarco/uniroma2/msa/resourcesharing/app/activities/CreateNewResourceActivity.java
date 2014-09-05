package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.joda.time.DateTime;

import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.ResourceIntentService;

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
    View progressView;

    @InjectView(R.id.create_new_resource_form)
    View createNewResourceFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_resource);
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

            Resource resource = new Resource(title, description, location, DateTime.now(), acquisitionMode, userService.readRegisteredUserId(), Resource.ResourceType.CREATED_BY_ME, false, false);
            ResourceIntentService.startActionSaveResource(this, resource);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View getProgressView() {
        return progressView;
    }

    @Override
    public View getContentView() {
        return createNewResourceFormView;
    }
}
