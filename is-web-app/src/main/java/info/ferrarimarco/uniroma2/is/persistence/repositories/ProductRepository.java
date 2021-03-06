package info.ferrarimarco.uniroma2.is.persistence.repositories;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends EntityRepository<Product>, MongoRepository<Product, String> {
    List<Product> findByCategory(Category category);
    Page<Product> findByCategory(Category category, Pageable pageable);
    List<Product> findByClazz(Clazz clazz);
    Page<Product> findByClazz(Clazz clazz, Pageable pageable);
    Long countByCategory(Category category);
    Long countByClazz(Clazz clazz);
}