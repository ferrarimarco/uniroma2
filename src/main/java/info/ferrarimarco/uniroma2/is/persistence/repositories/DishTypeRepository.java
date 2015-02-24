package info.ferrarimarco.uniroma2.is.persistence.repositories;

import info.ferrarimarco.uniroma2.is.model.DishType;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DishTypeRepository extends MongoRepository<DishType, String>  {
}
