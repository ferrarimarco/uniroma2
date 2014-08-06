package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.adapters.ResourceArrayAdapter;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.ResourceType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.AdapterModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.ContextModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.DaoModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.tasks.resource.ReadAllResourcesAsyncTask;

public class ShowResourcesActivity extends AbstractAsyncTaskActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @InjectView(R.id.resources_list_view)
    ListView resourcesListView;

    @InjectView(R.id.show_resources_progress)
    View mProgressView;

    private ReadAllResourcesAsyncTask readAllResourcesAsyncTask;

    private ResourceArrayAdapter resourceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_resources);

        ButterKnife.inject(this);

        this.defaultInitialization(mProgressView, resourcesListView);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the dropdown list navigation in the action bar.
        ResourceType[] resourceTypes = ResourceType.values();
        String[] resourceTypeLabels = new String[resourceTypes.length];

        for(ResourceType resourceType : resourceTypes){
            switch (resourceType){
                case NEW:
                    resourceTypeLabels[ResourceType.NEW.ordinal()] = getString(R.string.title_section_new_resource);
                    break;
                case CREATED_BY_ME:
                    resourceTypeLabels[ResourceType.CREATED_BY_ME.ordinal()] = getString(R.string.title_section_created_by_me_resource);
            }
        }

        actionBar.setListNavigationCallbacks(
                new ArrayAdapter<>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        resourceTypeLabels),
                this);

        // Check if there is already a defined user
        objectGraph = ObjectGraph.create(new ContextModuleImpl(this.getApplicationContext()), new DaoModuleImpl(), new AdapterModuleImpl(this));
        objectGraph.inject(this);

        resourceArrayAdapter = objectGraph.get(ResourceArrayAdapter.class);
        resourcesListView.setAdapter(resourceArrayAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        if(ResourceType.CREATED_BY_ME.ordinal() == position){
            loadResources(ResourceType.CREATED_BY_ME);
        }else if(ResourceType.NEW.ordinal() == position){
            loadResources(ResourceType.NEW);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_resource:
                Intent intent = new Intent(this, CreateNewResourceActivity.class);
                startActivity(intent);
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
        }

        readAllResourcesAsyncTask.execute();
    }

    @Override
    public void onBackgroundTaskCompleted(Object result) {
        readAllResourcesAsyncTask = null;

        ResourceTaskResult taskResult = (ResourceTaskResult) result;

        if (taskResult.getTaskResultType().equals(TaskResultType.SUCCESS)) {
            resourceArrayAdapter.clear();
            resourceArrayAdapter.addAll(taskResult.getResources());
        } else {
            // TODO: handle this error condition
        }

        showProgress(false);
    }

    @Override
    public void onBackgroundTaskCancelled() {
        readAllResourcesAsyncTask = null;
        showProgress(false);
    }
}
