package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.config.SpringMongoConfig;
import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.UserRepository;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceService {
	
	private AbstractApplicationContext context;
	private UserRepository repository;
	
	public void open(boolean deleteAllExisting){
		context = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
        repository = context.getBean(UserRepository.class);

        if(deleteAllExisting){
        	repository.deleteAll();
        }
	}
	
	public void close(){
		context.close();
	}
	
	public void storeUser(User user){
		repository.save(user);
	}
	
	public User readUser(String userName){
		return repository.findByUserName(userName);
	}
	
	public List<User> readAllUsers(){
		return repository.findAll();
	}
}
