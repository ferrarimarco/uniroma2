package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.CurrentHeartbeatSessionRepository;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.CurrentHeartbeatSession;

public class CurrentHeartbeatSessionPerisistenceService extends AbstractPersistenceService{

	private CurrentHeartbeatSessionRepository repository;
	
	@Override
	public void open() {
		repository = context.getBean(CurrentHeartbeatSessionRepository.class);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public CurrentHeartbeatSession readCurrentHeartbeatSession(String userName) {
		return repository.findByUserName(userName);
	}
	
	public CurrentHeartbeatSession storeCurrentHeartbeatSession(CurrentHeartbeatSession currentHeartbeatSession) {
		return repository.save(currentHeartbeatSession);
	}
	
}
