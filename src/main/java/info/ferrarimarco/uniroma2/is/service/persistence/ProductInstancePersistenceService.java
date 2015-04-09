package info.ferrarimarco.uniroma2.is.service.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import info.ferrarimarco.uniroma2.is.model.ProductInstance;

public interface ProductInstancePersistenceService extends EntityPersistenceService<ProductInstance>{
    Long countByProductId(String productId);
    Long countInstancesByProductId(String productId);
    Page<ProductInstance> findByProductId(String productId, Pageable pageable);
}
