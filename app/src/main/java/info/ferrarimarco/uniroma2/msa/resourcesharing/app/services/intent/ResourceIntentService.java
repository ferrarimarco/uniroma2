package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import dagger.ObjectGraph;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskResult;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.ResourceTaskType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.task.TaskResultType;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.gcm.GcmMessagingServiceImpl;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.persistence.ResourceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class ResourceIntentService extends IntentService{

    private static final String ACTION_SAVE_RESOURCE = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.action.SAVE_RESOURCE";

    private static final String EXTRA_PARAM_RESOURCE_TO_SAVE = "info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.intent.extra.RESOURCE_TO_SAVE";

    @Inject
    ResourceService resourceService;

    @Inject
    GcmMessagingServiceImpl gcmMessagingService;

    @Override
    public void onCreate(){
        super.onCreate();
        ObjectGraph objectGraph = ObjectGraphUtils.getObjectGraph(this);
        objectGraph.inject(this);
    }

    /**
     * Starts this service to perform action SAVE_RESOURCE with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSaveResource(Context context, Resource resource){
        Intent intent = new Intent(context, ResourceIntentService.class);
        intent.setAction(ACTION_SAVE_RESOURCE);
        intent.putExtra(EXTRA_PARAM_RESOURCE_TO_SAVE, resource);
        context.startService(intent);
    }

    public ResourceIntentService(){
        super("ResourceIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_SAVE_RESOURCE.equals(action)){
                final Resource resource = intent.getParcelableExtra(EXTRA_PARAM_RESOURCE_TO_SAVE);
                handleActionSaveResource(resource);
            }
        }
    }

    /**
     * Handle action Save resource in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveResource(Resource resource){
        ResourceTaskResult resourceTaskResult = resourceService.saveResourceLocal(resource);

        if(TaskResultType.FAILURE.equals(resourceTaskResult.getTaskResultType())){
            throw new RuntimeException("Unable to save the resource into local storage");
        }

        resourceService.readResourcesFromLocalStorage(ResourceTaskType.READ_CREATED_BY_ME_RESOURCES_LOCAL);

        gcmMessagingService.sendNewResource(resource);
    }
}
