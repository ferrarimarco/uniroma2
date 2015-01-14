package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import info.ferrarimarco.uniroma2.msa.resourcesharing.dao.repositories.mongodb.UserRepository;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DistanceService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService extends AbstractMongoPersistenceService {

	private UserRepository repository;

	@Autowired
	private DistanceService distanceService;

	public UserPersistenceService() {
		super();
	}

	@Override
	protected void open() {
		repository = context.getBean(UserRepository.class);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	public ResourceSharingUser storeUser(ResourceSharingUser user) {
		return repository.save(user);
	}

	public ResourceSharingUser readUsersByUserId(String userId) {
		return repository.findByUserId(userId);
	}

	public List<ResourceSharingUser> findAll() {
		return repository.findAll();
	}

	public List<ResourceSharingUser> findUsersInRange(Double latitude, Double longitude, ResourceSharingUser creator, ResourceSharingResource newResource) {
		List<ResourceSharingUser> users = findAll();
		List<ResourceSharingUser> usersInRange = new ArrayList<>();
		
		// Calculate max distance from resource in Km (1Km/10min)
		// minimum is 1 Km
		double maxDistanceFromResource;
		double multiplier = newResource.getTtl() / 1000 / 60 / 10;
		
		maxDistanceFromResource = multiplier > 1.0 ? 1.0 * multiplier : 1.0;

		for (ResourceSharingUser user : users) {
			Double distanceFromCurrentPosition = distanceService.calculateDistance(latitude, longitude, user.getLatitude(), user.getLongitude());
			if(distanceFromCurrentPosition <= maxDistanceFromResource){
				usersInRange.add(user);
			}
		}

		return usersInRange;
	}
}
