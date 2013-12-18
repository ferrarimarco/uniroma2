package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

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
	
	public HeartbeatSessionValue readHeartbeatSessionValue(int sequenceNumber) {
		return repository.findOne(sequenceNumber);
	}
	
	public void deleteHeartbeatSessionValue(int sequenceNumber) {
		repository.delete(sequenceNumber);
	}

}
