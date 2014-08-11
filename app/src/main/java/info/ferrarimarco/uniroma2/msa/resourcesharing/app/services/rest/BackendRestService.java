package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.rest;

import org.joda.time.DateTime;

import java.util.List;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

// TODO: is this needed? We can use GCM for everything :)
public interface BackendRestService {
    List<Resource> getAllResources();

    List<Resource> getAllResourcesSinceDate(DateTime since);

    Resource getResource(String backendId);

    Resource putResource(Resource resource);

    Resource deleteResource(Resource resource);

    Resource updateResource(Resource resource);
}
