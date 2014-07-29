package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import retrofit.RestAdapter;

public class ResourceSharingServiceImpl {

    private ResourceSharingServiceImpl service;

    @Inject
    public ResourceSharingServiceImpl(Context context) {

        Resources res = context.getResources();
        String serviceEndpoint = res.getString(R.string.resource_sharing_service_endpoint);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(serviceEndpoint).build();
        service = restAdapter.create(ResourceSharingServiceImpl.class);
    }

    public Resource getResourceById(String id) {
        return service.getResourceById(id);
    }

    public User getUserDetails(String userName) {
        return service.getUserDetails(userName);
    }
}
