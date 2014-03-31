package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.User;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ResourceSharingService {

	@GET(value = "/client/resource/{resourceId}")
	Resource getResourceById(@Path("resourceId") String id);

	@GET(value = "/client/user/{userName}")
	public User getUserDetails(@Path("userName") String userName);
}
