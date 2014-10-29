package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb.UserRepository;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DistanceService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService extends AbstractMongoPersistenceService{

	private UserRepository repository;
	
	@Autowired
	private DistanceService distanceService;
	
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
	
	public List<ResourceSharingUser> findUsersInRange(Double latitude, Double longitude){
		List<ResourceSharingUser> users = findAll();
		List<ResourceSharingUser> usersInRange = new ArrayList<>();
		
		for(ResourceSharingUser user : users) {
			Double distanceFromCurrentPosition = distanceService.calculateDistance(latitude, longitude, user.getLatitude(), user.getLongitude());
			if(user.getMaxDistance() != null && distanceFromCurrentPosition <= user.getMaxDistance()) {
				users.add(user);
			}
		}
		
		return usersInRange;
	}
}
