package info.ferrarimarco.uniroma2.is.persistence.repositories;

import info.ferrarimarco.uniroma2.is.model.EntityStat;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityStatRepository extends EntityRepository<EntityStat>,  MongoRepository<EntityStat, String> {
}
