package info.ferrarimarco.uniroma2.sii.heartmonitor.controllers;

import java.util.List;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.config.SpringMongoConfig;
import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.HeartbeatSessionRepository;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;


public class HeartbeatSessionPersistenceController {

	private AbstractApplicationContext context;
	private HeartbeatSessionRepository repository;
	
	public void open(boolean deleteAllExisting){
		context = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
        repository = context.getBean(HeartbeatSessionRepository.class);

        if(deleteAllExisting){
        	repository.deleteAll();
        }
	}
	
	public void close(){
		context.close();
	}
	
	public void storeHeartbeatSession(HeartbeatSession session){
		repository.save(session);
	}
	
	public HeartbeatSession readHeartbeatSession(String sessionId){
		return repository.findById(sessionId);
	}
	
	public List<HeartbeatSession> readAllHeartbeatSessions(){
		return repository.findAll();
	}	
	
}
