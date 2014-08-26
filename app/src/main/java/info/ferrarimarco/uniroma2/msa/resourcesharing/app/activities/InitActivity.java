package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisteredUserCheckAsyncTask;

public class InitActivity extends AbstractAsyncTaskActivity {

    private RegisteredUserCheckAsyncTask registeredUserCheckTask;

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (this.getActionBar() != null) {
            this.getActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        // check for Google Play Services
        checkGooglePlayServicesInstallationStatus();

        objectGraph = ObjectGraph.create(new ContextModuleImpl(this.getApplicationContext()), new DaoModuleImpl());
        objectGraph.inject(this);

        // Create an async task to check for reg user, with this param
        checkRegisteredUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.init, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkRegisteredUser() {
        if (registeredUserCheckTask != null) {
            return;
        }

        registeredUserCheckTask = objectGraph.get(RegisteredUserCheckAsyncTask.class);
        registeredUserCheckTask.initTask(this, this.getApplicationContext());
        registeredUserCheckTask.execute(getResources().getString(R.string.registered_user_id));
    }

    public void checkGooglePlayServicesInstallationStatus() {
        // Check status of Google Play Services
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        try {
            if (status != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(status, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            }
        } catch (Exception e) {
            Log.e("Error: GooglePlayServiceUtil: ", "" + e);
        }
    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {

        UserTaskResult taskResult = (UserTaskResult) result;

        if (taskResult.getTaskResult().equals(TaskResult.SUCCESS)) {
            if (taskResult.isRegisteredUserPresent()) {
                Intent intent = new Intent(this, ShowResourcesActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, RegisterNewUserActivity.class);
                startActivity(intent);
            }
        } else {
            // TODO: handle error condition
        }

        finish();
    }

    @Override
    public void onBackgroundTaskCancelled(Object cancelledTask) {
        registeredUserCheckTask = null;
    }
}
