package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.UserIntentService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.ResourceService;

import static info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource.ResourceType.CREATED_BY_ME;
import static info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource.ResourceType.NEW;

public class ShowResourcesActivity extends AbstractActivity implements ActionBar.OnNavigationListener, SwipeRefreshLayout.OnRefreshListener, LocationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @InjectView(R.id.resources_list_view)
    ListView resourcesListView;

    @InjectView(R.id.show_resources_layout)
    SwipeRefreshLayout swipeLayout;

    @Inject
    ResourceService resourceService;

    private ResourceArrayAdapter resourceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                        break;
                    case BOOKED_BY_ME:
                        resourceTypeLabels[Resource.ResourceType.CREATED_BY_ME.ordinal()] = getString(R.string.title_section_booked_resource);
                        break;
                }
            }

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setListNavigationCallbacks(new ArrayAdapter<>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1, resourceTypeLabels), this);
        }

        resourceArrayAdapter = new ResourceArrayAdapter(this);
        resourcesListView.setAdapter(resourceArrayAdapter);

        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        // Request for location updates
        // TODO: configure this request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
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
    public void onRefresh() {
        if (getActionBar() != null) {
            loadResources(getActionBar().getSelectedNavigationIndex());
        }
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        loadResources(position);
        return true;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (getActionBar() != null && savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getActionBar() != null) {
            // Serialize the current dropdown position.
            outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
        }
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

    private void loadResources(int resourceTypeId) {
        swipeLayout.setRefreshing(true);

        Resource.ResourceType resourceType = NEW;

        if (CREATED_BY_ME.ordinal() == resourceTypeId) {
            resourceType = CREATED_BY_ME;
        } else if (NEW.ordinal() == resourceTypeId) {
            resourceType = NEW;
        }

        switch (resourceType) {
            case NEW:
                resourceService.readResourcesFromLocalStorage(ResourceTaskType.READ_NEW_RESOURCES_LOCAL);
                break;
            case CREATED_BY_ME:
                resourceService.readResourcesFromLocalStorage(ResourceTaskType.READ_CREATED_BY_ME_RESOURCES_LOCAL);
                break;
            case BOOKED_BY_ME:
                resourceService.readResourcesFromLocalStorage(ResourceTaskType.READ_BOOKED_BY_ME_RESOURCES);
                break;
        }
    }

    @Subscribe
    public void resourceListAvailable(ResourceListAvailableEvent event) {
        ResourceTaskResult result = event.getResult();

        if (getActionBar() != null) {
            if ((ResourceTaskType.READ_NEW_RESOURCES_LOCAL.equals(result.getTaskType()) && getActionBar().getSelectedNavigationIndex() == Resource.ResourceType.NEW.ordinal())
                    || (ResourceTaskType.READ_CREATED_BY_ME_RESOURCES_LOCAL.equals(result.getTaskType()) && getActionBar().getSelectedNavigationIndex() == Resource.ResourceType.CREATED_BY_ME.ordinal())
                    || (ResourceTaskType.READ_BOOKED_BY_ME_RESOURCES.equals(result.getTaskType()) && getActionBar().getSelectedNavigationIndex() == Resource.ResourceType.BOOKED_BY_ME.ordinal())) {
                if (TaskResultType.SUCCESS.equals(result.getTaskResultType())) {
                    resourceArrayAdapter.clear();
                    resourceArrayAdapter.addAll(result.getResources());
                    resourceArrayAdapter.notifyDataSetChanged();
                } else if (TaskResultType.FAILURE.equals(result.getTaskResultType())) {
                    throw new IllegalStateException("Unable to read from internal data storage");
                }
            }
        }

        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        UserIntentService.startActionUpdateUserInfo(this, location);
    }
}
