package info.ferrarimarco.uniroma2.is.persistence.repositories;

import info.ferrarimarco.uniroma2.is.model.Instance;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface InstanceRepository extends MongoRepository<Instance, String> {
}