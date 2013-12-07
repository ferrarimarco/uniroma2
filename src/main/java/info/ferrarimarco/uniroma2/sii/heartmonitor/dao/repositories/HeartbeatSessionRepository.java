package info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HeartbeatSessionRepository extends MongoRepository<HeartbeatSession,String> {

	public HeartbeatSession findById(String id);
	
	public Iterable<HeartbeatSession> findByUserId(String userId);
	
	public Iterable<HeartbeatSession> findByShortId(String shortSessionId);
	
}
