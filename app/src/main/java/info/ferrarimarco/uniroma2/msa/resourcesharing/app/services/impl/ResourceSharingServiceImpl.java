package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.ResourceSharingService;
import retrofit.RestAdapter;

public class ResourceSharingServiceImpl implements ResourceSharingService {

	private ResourceSharingService service;

	@Inject
	public ResourceSharingServiceImpl(Context context) {

		Resources res = context.getResources();
		XmlResourceParser xrp = res.getXml(R.xml.config);

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("https://api.github.com").build();

		service = restAdapter.create(ResourceSharingService.class);
	}

	@Override
	public Resource getResourceById(String id) {
		return service.getResourceById(id);
	}

	@Override
	public User getUserDetails(String userName) {
		return service.getUserDetails(userName);
	}
}
