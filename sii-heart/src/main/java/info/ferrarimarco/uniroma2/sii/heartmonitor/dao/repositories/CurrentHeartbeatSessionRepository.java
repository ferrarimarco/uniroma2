package info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.CurrentHeartbeatSession;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CurrentHeartbeatSessionRepository extends MongoRepository<CurrentHeartbeatSession,String> {
	
	public CurrentHeartbeatSession findByUserName(String userName);

}
