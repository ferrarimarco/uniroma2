package info.ferrarimarco.uniroma2.is.persistence.repositories;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.ProductInstance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductInstanceRepository extends EntityRepository<ProductInstance>,  MongoRepository<ProductInstance, String> {
    Long countByProductId(String productId);
    List<ProductInstance> findByProductId(String productId);
    Page<ProductInstance> findByProductId(String productId, Pageable pageable);
}
