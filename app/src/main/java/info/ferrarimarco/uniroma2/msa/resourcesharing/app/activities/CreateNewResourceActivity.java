package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;

import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.ResourceIntentService;

public class CreateNewResourceActivity extends AbstractActivity implements AdapterView.OnItemSelectedListener {

    @InjectView(R.id.resourceTitleEditText)
    EditText titleEditText;

    @InjectView(R.id.resourceDescriptionEditText)
    EditText descriptionEditText;

    @InjectView(R.id.resourceAcquisitionModeEditText)
    EditText acquisitionModeEditText;

    @InjectView(R.id.resource_ttl_amount)
    EditText ttlAmountEditText;

    @InjectView(R.id.resource_ttl_spinner)
    Spinner ttlSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_resource);
    }

    @Override
    protected Class getRedirectActivityClass() {
        return null;
    }

    @Override
    protected boolean terminateActivityAfterRedirect() {
        return false;
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
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String acquisitionMode = acquisitionModeEditText.getText().toString();
            Long ttlAmount = Long.parseLong(ttlAmountEditText.getText().toString());
            String selectedTtlUnit = ttlSpinner.getSelectedItem().toString();

            // In secs
            Long resourceTtl = ttlAmount;

            if (selectedTtlUnit.equals(getResources().getString(R.string.minutes))) {
                resourceTtl *= 60;
            } else if (selectedTtlUnit.equals(getResources().getString(R.string.hours))) {
                resourceTtl *= 60 * 60;
            } else if (selectedTtlUnit.equals(getResources().getString(R.string.days))) {
                resourceTtl *= 60 * 60 * 24;
            } else {
                throw new IllegalArgumentException("Unable to choose a TTL unit measure");
            }

            Resource resource = new Resource(title, description, this.getLastKnownLocation(), DateTime.now(), acquisitionMode, userService.readRegisteredUserId(), Resource.ResourceType.CREATED_BY_ME, false, resourceTtl, null);
            ResourceIntentService.startActionSaveResourceFromMe(getApplicationContext(), resource);
            Toast.makeText(this, getResources().getString(R.string.resource_save_completed), Toast.LENGTH_SHORT).show();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private Location getLastKnownLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
