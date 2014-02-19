package info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingMobileNodeData;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface MobileNodeDataRepository extends MongoRepository<ResourceSharingMobileNodeData,String> {
	
	ResourceSharingMobileNodeData findByMobileNodeId(String id);
}
