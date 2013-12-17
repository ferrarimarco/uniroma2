package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.UserRepository;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;

import java.util.List;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService extends AbstractPersistenceService{

	private AbstractApplicationContext context;
	private UserRepository repository;

	@Override
	public void open(){
		repository = context.getBean(UserRepository.class);
	}
	
	@Override
	public void deleteAll() {
		repository.deleteAll();		
	}

	public User storeUser(User user){
		return repository.save(user);
	}

	public User storeUser(String userName, String hashedPassword) {
		User newUser = new User(userName, hashedPassword);

		return storeUser(newUser);
	}

	public User readUser(String userName){
		return repository.findByUserName(userName);
	}

	public List<User> readAllUsers(){
		return repository.findAll();
	}
}
