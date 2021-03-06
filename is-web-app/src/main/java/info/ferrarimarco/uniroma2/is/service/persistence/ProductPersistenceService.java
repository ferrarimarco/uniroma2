package info.ferrarimarco.uniroma2.is.service.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Product;

public interface ProductPersistenceService extends EntityPersistenceService<Product>{
    List<Product> findByCategory(Category category);
    Page<Product> findByCategory(Category category, Pageable pageable);
    List<Product> findByClazz(Clazz clazz);
    Page<Product> findByClazz(Clazz clazz, Pageable pageable);
    Long countByCategory(Category category);
    Long countByClazz(Clazz clazz);
}
