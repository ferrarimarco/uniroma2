package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.callers.AsyncCaller;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.UserTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.user.RegisteredUserCheckAsyncTask;

public class InitActivity extends Activity implements AsyncCaller {

    private ObjectGraph objectGraph;

    private RegisteredUserCheckAsyncTask registeredUserCheckTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (this.getActionBar() != null) {
            this.getActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        // Check if there is already a defined user
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

    @Override
    public void onBackgroundTaskCompleted(Object result) {

        UserTaskResult taskResult = (UserTaskResult) result;

        if (taskResult.getTaskResultType().equals(TaskResultType.SUCCESS)) {
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
    public void onBackgroundTaskCancelled() {
        registeredUserCheckTask = null;
    }
}
