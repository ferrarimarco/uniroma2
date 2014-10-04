package info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<ResourceSharingUser, String> {
	
	public List<ResourceSharingUser> findByName(String name);
	public List<ResourceSharingUser> findByEmail(String email);
}
