package info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;

public interface HeartbeatSessionRepository extends MongoRepository<HeartbeatSession, String> {

	public HeartbeatSession findById(String id);
	
	public List<HeartbeatSession> findByUserName(String userName);
	
	public List<HeartbeatSession> findByUserNameAndIsClosedTrue(String userName);
	
}
