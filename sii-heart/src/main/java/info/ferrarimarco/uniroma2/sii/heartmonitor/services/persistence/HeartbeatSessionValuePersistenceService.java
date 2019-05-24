package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

import java.util.List;

import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.HeartbeatSessionValueRepository;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSessionValue;

@Service
public class HeartbeatSessionValuePersistenceService extends AbstractPersistenceService {

	private HeartbeatSessionValueRepository repository;
	
	public HeartbeatSessionValuePersistenceService() {
		super();
	}
	
	@Override
	protected void open() {
		repository = context.getBean(HeartbeatSessionValueRepository.class);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}
	
	public HeartbeatSessionValue storeHeartbeatSessionValue(HeartbeatSessionValue heartbeatSessionValue) {
		return repository.save(heartbeatSessionValue);
	}
	
	public HeartbeatSessionValue readHeartbeatSessionValue(String id) {
		return repository.findOne(id);
	}
	
	public void deleteHeartbeatSessionValue(String id) {
		repository.delete(id);
	}
	
	public void deleteAllHeartbeatSessionValuesByReferringSessionId(String referringSessionId) {
		List<HeartbeatSessionValue> entities = repository.findByReferringSessionId(referringSessionId);
		repository.delete(entities);
	}
	
	public void deleteAllHeartbeatSessionValuesByReferringUserName(String referringUserName) {
		List<HeartbeatSessionValue> entities = repository.findByReferringUserName(referringUserName);
		repository.delete(entities);
	}

}
