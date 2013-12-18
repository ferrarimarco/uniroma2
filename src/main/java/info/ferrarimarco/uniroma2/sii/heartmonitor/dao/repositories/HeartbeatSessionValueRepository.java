package info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSessionValue;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HeartbeatSessionValueRepository extends MongoRepository<HeartbeatSessionValue, Integer> {

}
