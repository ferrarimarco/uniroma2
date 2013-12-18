package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.HeartbeatSessionRepository;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HeartbeatSessionPersistenceService extends AbstractPersistenceService {
	
	private HeartbeatSessionRepository repository;
	
	public HeartbeatSessionPersistenceService() {
		super();
	}

	@Override
	protected void open(){
        repository = context.getBean(HeartbeatSessionRepository.class);
	}
	
	@Override
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public HeartbeatSession storeHeartbeatSession(HeartbeatSession session){
		return repository.save(session);
	}
	
	public HeartbeatSession readHeartbeatSession(String sessionId){
		return repository.findById(sessionId);
	}
	
	public List<HeartbeatSession> readHeartbeatSessions(String userName){
		return repository.findByUserName(userName);
	}
	
	public List<HeartbeatSession> readAllHeartbeatSessions(){
		return repository.findAll();
	}
}
