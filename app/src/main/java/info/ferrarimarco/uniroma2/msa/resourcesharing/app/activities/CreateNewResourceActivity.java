package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;

import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.ResourceIntentService;

public class CreateNewResourceActivity extends AbstractActivity{

    @InjectView(R.id.resourceTitleEditText)
    EditText titleEditText;

    @InjectView(R.id.resourceDescriptionEditText)
    EditText descriptionEditText;

    @InjectView(R.id.resourceAcquisitionModeEditText)
    EditText acquisitionModeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_resource);
    }

    @Override
    protected Class getRedirectActivityClass(){
        return null;
    }

    @Override
    protected boolean terminateActivityAfterRedirect(){
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_new_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_save_new_resource){
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String acquisitionMode = acquisitionModeEditText.getText().toString();

            Resource resource = new Resource(title, description, this.getLastKnownLocation(), DateTime.now(), acquisitionMode, userService.readRegisteredUserId(), Resource.ResourceType.CREATED_BY_ME, false, timeToLive);
            ResourceIntentService.startActionSaveResource(getApplicationContext(), resource);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private Location getLastKnownLocation(){
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }
}
