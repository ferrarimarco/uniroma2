package info.ferrarimarco.uniroma2.is.service.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;

public interface ClazzPersistenceService extends EntityPersistenceService<Clazz>{
    List<Clazz> findByCategory(Category category);
    Page<Clazz> findByCategory(Category category, Pageable pageable);
}
