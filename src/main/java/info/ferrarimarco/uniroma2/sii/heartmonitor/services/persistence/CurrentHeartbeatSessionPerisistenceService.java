package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.CurrentHeartbeatSessionRepository;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.CurrentHeartbeatSession;

public class CurrentHeartbeatSessionPerisistenceService extends AbstractPersistenceService{

	private Logger logger = LoggerFactory.getLogger(CurrentHeartbeatSessionPerisistenceService.class);
	
	private CurrentHeartbeatSessionRepository repository;
	
	public CurrentHeartbeatSessionPerisistenceService() {
		super();
	}
	
	@Override
	protected void open() {
		repository = context.getBean(CurrentHeartbeatSessionRepository.class);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public void deleteCurrentHeartbeatSession(CurrentHeartbeatSession currentHeartbeatSession) {
		repository.delete(currentHeartbeatSession);
	}
	
	public void deleteCurrentHeartbeatSession(String currentHeartbeatSessionId) {
		repository.delete(currentHeartbeatSessionId);
	}
	
	public CurrentHeartbeatSession readCurrentHeartbeatSession(String userName) {
		return repository.findByUserName(userName);
	}
	
	public CurrentHeartbeatSession readCurrentHeartbeatSession() {
		
		List<CurrentHeartbeatSession> currentHeartbeatSessions = readAllCurrentHeartbeatSessions();
		
		CurrentHeartbeatSession currentHeartbeatSession = null;
		
		if(currentHeartbeatSessions.size() == 1) {
			currentHeartbeatSession = currentHeartbeatSessions.get(0);
		}else {
			if(currentHeartbeatSessions.size() > 1) {
				deleteAll();
				logger.error("Found more than one session waiting for values. Delete every leftover session.");
			}
		}
		
		return currentHeartbeatSession;
	}
	
	public CurrentHeartbeatSession storeCurrentHeartbeatSession(CurrentHeartbeatSession currentHeartbeatSession) {
		return repository.save(currentHeartbeatSession);
	}
	
	public List<CurrentHeartbeatSession> readAllCurrentHeartbeatSessions(){
		return repository.findAll();
	}
	
}
