package info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<ResourceSharingUser, String> {
    public ResourceSharingUser findByUserId(String userId);
}
