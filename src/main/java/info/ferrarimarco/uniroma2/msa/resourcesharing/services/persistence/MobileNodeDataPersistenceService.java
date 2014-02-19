package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import java.util.List;

import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb.MobileNodeDataRepository;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingMobileNodeData;

@Service
public class MobileNodeDataPersistenceService extends AbstractMongoPersistenceService {
	
	private MobileNodeDataRepository repository;
	
	public MobileNodeDataPersistenceService() {
		super();
	}

	@Override
	protected void open() {
		repository = context.getBean(MobileNodeDataRepository.class);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	@Override
	public void dropCollection() {
		super.dropCollection(ResourceSharingMobileNodeData.class);
	}
	
	public ResourceSharingMobileNodeData findById(String mobileNodeId) {
		return repository.findByMobileNodeId(mobileNodeId);
	}
	
	public ResourceSharingMobileNodeData storeMobileNodeData(ResourceSharingMobileNodeData mobileNodeData) {
		return repository.save(mobileNodeData);
	}

	public List<ResourceSharingMobileNodeData> findAll() {
		return repository.findAll();
	}
}
