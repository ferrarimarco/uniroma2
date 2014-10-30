package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb.ResourceRepository;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourcePersistenceService extends AbstractMongoPersistenceService {

    private ResourceRepository repository;

    @Autowired
    private HashingService hashingService;

    @Autowired
    private DatatypeConversionService datatypeConversionService;

    public ResourcePersistenceService() {
        super();
    }

    @Override
    protected void open() {
        repository = context.getBean(ResourceRepository.class);
    }

    public ResourceSharingResource storeResource(ResourceSharingResource resource) {
        if (resource.getId() == null || resource.getId().length() == 0) {
            resource.setId(this.createResourceId(Long.toString(resource.getCreationTime().getMillis()), resource.getCreatorId()));
        }
        return repository.save(resource);
    }

    public List<ResourceSharingResource> readResourcesByCreatorId(String creatorId) {
        return repository.findByCreatorId(creatorId);
    }

    public ResourceSharingResource readResourceById(String resourceId) {
        return repository.findById(resourceId);
    }

    public ResourceSharingResource readResourceById(String creationTimeMs, String creatorId) {
        return repository.findById(createResourceId(creationTimeMs, creatorId));
    }

    public List<ResourceSharingResource> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteResource(String resourceId) {
        repository.delete(resourceId);
    }

    public void deleteResource(String creationTimeMs, String creatorId) {
        repository.delete(createResourceId(creationTimeMs, creatorId));
    }

    public String createResourceId(String creationTimeMs, String creatorId) {
        byte[] hashedPasswordBytes = hashingService.hash(creationTimeMs + creatorId);
        return datatypeConversionService.bytesToHexString(hashedPasswordBytes);
    }
}
