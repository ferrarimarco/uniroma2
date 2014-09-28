package info.ferrarimarco.uniroma2.msa.resourcesharing.app.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnItemClick;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.adapters.ResourceArrayAdapter;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.event.ResourceListAvailableEvent;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.LocationTrackingIntentService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.ResourceIntentService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.ResourceService;

import static info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource.ResourceType.BOOKED_BY_ME;
import static info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource.ResourceType.CREATED_BY_ME;
import static info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource.ResourceType.CREATED_BY_OTHERS;

public class ShowResourcesActivity extends AbstractActivity implements ActionBar.OnNavigationListener, SwipeRefreshLayout.OnRefreshListener{

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

    private LocationRequest balancedPowerlocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.activity_show_resources);
        super.onCreate(savedInstanceState);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();

        if(actionBar != null){
            // Set up the dropdown list navigation in the action bar.
            Resource.ResourceType[] resourceTypes = Resource.ResourceType.values();
            String[] resourceTypeLabels = new String[resourceTypes.length];

            for(Resource.ResourceType resourceType : resourceTypes){
                switch(resourceType){
                    case CREATED_BY_OTHERS:
                        resourceTypeLabels[Resource.ResourceType.CREATED_BY_OTHERS.ordinal()] = getString(R.string.title_section_new_resource);
                        break;
                    case CREATED_BY_ME:
                        resourceTypeLabels[Resource.ResourceType.CREATED_BY_ME.ordinal()] = getString(R.string.title_section_created_by_me_resource);
                        break;
                    case BOOKED_BY_ME:
                        resourceTypeLabels[Resource.ResourceType.BOOKED_BY_ME.ordinal()] = getString(R.string.title_section_booked_resource);
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

        balancedPowerlocationRequest = LocationRequest.create();
        balancedPowerlocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        balancedPowerlocationRequest.setInterval(1000 * 60 * 60);
        balancedPowerlocationRequest.setFastestInterval(1000 * 5);
    }

    @Override
    public void onConnected(Bundle arg0){
        // Request for location updates
        Intent locationTrackingIntent = new Intent(this, LocationTrackingIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, locationTrackingIntent, 0);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, balancedPowerlocationRequest, pendingIntent);
    }

    @Override
    protected void onPause(){
        if(googleApiClient.isConnected()){
            Intent locationTrackingIntent = new Intent(this, LocationTrackingIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 1, locationTrackingIntent, 0);
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, pendingIntent);
        }

        super.onPause();
    }

    @Override
    public void onRefresh(){
        if(getActionBar() != null){
            loadResources(getActionBar().getSelectedNavigationIndex());
        }
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id){
        loadResources(position);
        return true;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        // Restore the previously serialized current dropdown position.
        if(getActionBar() != null && savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)){
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if(getActionBar() != null){
            // Serialize the current dropdown position.
            outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_resources, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
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

    private void loadResources(int resourceTypeId){
        swipeLayout.setRefreshing(true);

        Resource.ResourceType resourceType = idToResourceType(resourceTypeId);

        switch(resourceType){
            case CREATED_BY_OTHERS:
                resourceService.readResourceLocal(new Resource(Resource.ResourceType.CREATED_BY_OTHERS), ResourceService.ResourceServiceOperationMode.ASYNC);
                break;
            case CREATED_BY_ME:
                resourceService.readResourceLocal(new Resource(Resource.ResourceType.CREATED_BY_ME), ResourceService.ResourceServiceOperationMode.ASYNC);
                break;
            case BOOKED_BY_ME:
                resourceService.readResourceLocal(new Resource(Resource.ResourceType.BOOKED_BY_ME), ResourceService.ResourceServiceOperationMode.ASYNC);
                break;
        }
    }

    private Resource.ResourceType idToResourceType(int resourceTypeId){
        if(CREATED_BY_ME.ordinal() == resourceTypeId){
            return CREATED_BY_ME;
        }else if(CREATED_BY_OTHERS.ordinal() == resourceTypeId){
            return CREATED_BY_OTHERS;
        }else if(BOOKED_BY_ME.ordinal() == resourceTypeId){
            return BOOKED_BY_ME;
        }

        return null;
    }

    @Subscribe
    public void resourceListAvailable(ResourceListAvailableEvent event){
        ResourceTaskResult result = event.getResult();

        if(getActionBar() != null){
            resourceArrayAdapter.clear();
            resourceArrayAdapter.addAll(result.getResources());
            resourceArrayAdapter.notifyDataSetChanged();
        }

        swipeLayout.setRefreshing(false);
    }

    @OnItemClick(R.id.resources_list_view)
    public void onResourceClick(int position){
        // Get clicked resource
        final Resource resource = resourceArrayAdapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(resource.getType()){
            case CREATED_BY_OTHERS:
                builder.setMessage(R.string.book_resource_dialog_message + " " + resource.getTitle()).setTitle(R.string.book_resource_dialog_message);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        ResourceIntentService.startActionBookResourceFromOthers(ShowResourcesActivity.this, resource);
                        dialog.dismiss();
                    }
                });
                break;
            case CREATED_BY_ME:
                builder.setMessage(resource.getTitle()).setTitle(R.string.delete_resource_dialog_message);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        ResourceIntentService.startActionDeleteResourceFromMe(ShowResourcesActivity.this, resource);
                        ShowResourcesActivity.this.resourceArrayAdapter.remove(resource);
                    }
                });
                break;
        }

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
