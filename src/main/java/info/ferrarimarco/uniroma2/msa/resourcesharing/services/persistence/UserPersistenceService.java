package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb.UserRepository;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService extends AbstractMongoPersistenceService{

	private UserRepository repository;
	
	public UserPersistenceService() {
		super();
	}
	
	@Override
	protected void open(){
		repository = context.getBean(UserRepository.class);
	}
	
	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	public ResourceSharingUser storeUser(ResourceSharingUser user){
		return repository.save(user);
	}

	public ResourceSharingUser readUsersByUserId(String userId){
		return repository.findByUserId(userId);
	}

	public List<ResourceSharingUser> findAll(){
		return repository.findAll();
	}
}
