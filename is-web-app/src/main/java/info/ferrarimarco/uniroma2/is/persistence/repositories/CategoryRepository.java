package info.ferrarimarco.uniroma2.is.persistence.repositories;

import info.ferrarimarco.uniroma2.is.model.Category;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends EntityRepository<Category>, MongoRepository<Category, String> {
}