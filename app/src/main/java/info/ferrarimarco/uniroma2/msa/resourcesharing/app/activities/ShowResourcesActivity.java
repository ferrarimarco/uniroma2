package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource.ReadAllResourcesAsyncTask;

public class ShowResourcesActivity extends Activity implements AsyncCaller, ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @InjectView(R.id.resources_view_switcher)
    ViewSwitcher resourceViewSwitcher;

    @InjectView(R.id.show_resources_progress)
    View mProgressView;

    private ObjectGraph objectGraph;

    private ReadAllResourcesAsyncTask readAllResourcesAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_resources);

        ButterKnife.inject(this);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                // New Resources
                                getString(R.string.title_section1),

                                // Resources created by me
                                getString(R.string.title_section2),

                                // Archived Resources
                                getString(R.string.title_section3),
                        }),
                this);

        // Check if there is already a defined user
        objectGraph = ObjectGraph.create(new ContextModuleImpl(this.getApplicationContext()), new DaoModuleImpl());
        objectGraph.inject(this);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {

        Toast.makeText(this, "Selected Section: " + (position + 1), Toast.LENGTH_LONG).show();

        switch (position) {
            case 0:
                loadResources(ResourceType.NEW);
                break;
            case 1:
                loadResources(ResourceType.CREATED_BY_ME);
                break;
            case 2:
                loadResources(ResourceType.ARCHIVED);
                break;
        }

        return true;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_resources, menu);
        return true;
    }

    @Inject
    GenericDao<Resource> resDao;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_resource:
                // Test the insertion of a new resource
                try {
                    resDao.open(Resource.class);
                    resDao.save(new Resource("'Title", "Desc", "LOC", DateTime.now(), "ACQ", "CREATOR", ResourceType.CREATED_BY_ME));
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    resDao.close();
                }
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadResources(ResourceType resourceType) {

        showProgress(true);

        readAllResourcesAsyncTask = objectGraph.get(ReadAllResourcesAsyncTask.class);
        readAllResourcesAsyncTask.initTask(this, this.getApplicationContext());

        switch (resourceType) {
            case NEW:
                readAllResourcesAsyncTask.setTaskType(ResourceTaskType.READ_NEW_RESOURCES);
                break;
            case CREATED_BY_ME:
                readAllResourcesAsyncTask.setTaskType(ResourceTaskType.READ_CREATED_BY_ME_RESOURCES);
                break;
            case ARCHIVED:
                readAllResourcesAsyncTask.setTaskType(ResourceTaskType.READ_ARCHIVED_RESOURCES);
                break;
        }

        readAllResourcesAsyncTask.execute();
    }

    /**
     * Shows the progress UI and hides the form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {
        readAllResourcesAsyncTask = null;
        showProgress(false);

        ResourceTaskResult taskResult = (ResourceTaskResult) result;

        if (taskResult.getTaskResultType().equals(TaskResultType.SUCCESS)) {
            Toast.makeText(this, "List Loaded", Toast.LENGTH_LONG).show();
        } else {
            // TODO: handle this error condition
        }
    }

    @Override
    public void onBackgroundTaskCancelled() {
        readAllResourcesAsyncTask = null;
        showProgress(false);
    }
}
