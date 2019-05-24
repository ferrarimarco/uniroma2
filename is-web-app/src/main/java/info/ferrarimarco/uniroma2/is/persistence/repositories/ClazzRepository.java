package info.ferrarimarco.uniroma2.is.persistence.repositories;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClazzRepository extends EntityRepository<Clazz>, MongoRepository<Clazz, String>{
    List<Clazz> findByCategory(Category category);
    Page<Clazz> findByCategory(Category category, Pageable pageable);
}
