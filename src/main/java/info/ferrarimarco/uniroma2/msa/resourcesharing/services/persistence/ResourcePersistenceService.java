package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb.ResourceRepository;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ResourcePersistenceService extends AbstractMongoPersistenceService{
	
	private ResourceRepository repository;
	
	public ResourcePersistenceService() {
		super();
	}

	@Override
	protected void open() {
		repository = context.getBean(ResourceRepository.class);
	}

	public ResourceSharingResource storeResource(ResourceSharingResource resource){
		return repository.save(resource);
	}

	public List<ResourceSharingResource> readResourcesByCreatorId(String creatorId){
		return repository.findByCreatorId(creatorId);
	}
	
	public ResourceSharingResource readResourceById(String resourceId) {
		return repository.findById(resourceId);
	}

	public List<ResourceSharingResource> findAll(){
		return repository.findAll();
	}

	@Override
	public void dropCollection() {
		super.dropCollection(ResourceSharingResource.class);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();		
	}

}
