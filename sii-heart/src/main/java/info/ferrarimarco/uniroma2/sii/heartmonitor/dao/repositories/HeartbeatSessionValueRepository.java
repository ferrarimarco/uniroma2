package info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories;

import java.util.List;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSessionValue;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HeartbeatSessionValueRepository extends MongoRepository<HeartbeatSessionValue, String> {
	
	public List<HeartbeatSessionValue> findByReferringSessionId(String referringSessionId);
	public List<HeartbeatSessionValue> findByReferringUserName(String referringUserName);
}
