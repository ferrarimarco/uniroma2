package info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String>{
	
	public User findByUserName(String userName);
}
