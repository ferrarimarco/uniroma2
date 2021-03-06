package info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResourceRepository extends MongoRepository<ResourceSharingResource, String> {

    List<ResourceSharingResource> findByCreatorId(String creatorId);

    ResourceSharingResource findById(String id);
}
