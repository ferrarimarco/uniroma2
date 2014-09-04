package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.InjectView;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.adapters.ResourceArrayAdapter;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ResourceListAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl.AdapterModuleImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.ResourceService;

import static info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource.ResourceType.CREATED_BY_ME;
import static info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource.ResourceType.NEW;

public class ShowResourcesActivity extends AbstractAsyncTaskActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @InjectView(R.id.resources_list_view)
    ListView resourcesListView;

    @InjectView(R.id.show_resources_progress)
    View progressView;

    @Inject
    ResourceService resourceService;

    private ResourceArrayAdapter resourceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        objectGraph = objectGraph.plus(new AdapterModuleImpl(this));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_resources);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            // Set up the dropdown list navigation in the action bar.
            Resource.ResourceType[] resourceTypes = Resource.ResourceType.values();
            String[] resourceTypeLabels = new String[resourceTypes.length];

            for (Resource.ResourceType resourceType : resourceTypes) {
                switch (resourceType) {
                    case NEW:
                        resourceTypeLabels[Resource.ResourceType.NEW.ordinal()] = getString(R.string.title_section_new_resource);
                        break;
                    case CREATED_BY_ME:
                        resourceTypeLabels[Resource.ResourceType.CREATED_BY_ME.ordinal()] = getString(R.string.title_section_created_by_me_resource);
                }
            }

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setListNavigationCallbacks(
                    new ArrayAdapter<>(
                            actionBar.getThemedContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            resourceTypeLabels),
                    this);
        }

        resourceArrayAdapter = objectGraph.get(ResourceArrayAdapter.class);
        resourcesListView.setAdapter(resourceArrayAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        if (CREATED_BY_ME.ordinal() == position) {
            loadResources(CREATED_BY_ME);
        } else if (NEW.ordinal() == position) {
            loadResources(NEW);
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

    private void loadResources(Resource.ResourceType resourceType) {
        showProgress(true);

        switch (resourceType) {
            case NEW:
                resourceService.readResourcesFromLocalStorage(ResourceTaskType.READ_NEW_RESOURCES);
                break;
            case CREATED_BY_ME:
                resourceService.readResourcesFromLocalStorage(ResourceTaskType.READ_CREATED_BY_ME_RESOURCES);
                break;
        }
    }

    @Subscribe
    public void resourceListAvailable(ResourceListAvailableEvent event) {
        ResourceTaskResult result = event.getResult();

        if (TaskResultType.SUCCESS.equals(result.getTaskResultType())) {
            resourceArrayAdapter.clear();
            resourceArrayAdapter.addAll(result.getResources());
        } else {
            // TODO: handle this error condition
        }

        showProgress(false);
    }


    @Override
    public View getProgressView() {
        return progressView;
    }

    @Override
    public View getContentView() {
        return resourcesListView;
    }
}
