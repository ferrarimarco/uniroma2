package info.ferrarimarco.uniroma2.is.persistence.repositories;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Instance;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface InstanceRepository extends MongoRepository<Instance, String> {
    List<Instance> findByInstanceClass(Clazz instanceClass);
}