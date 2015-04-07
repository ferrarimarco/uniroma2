package info.ferrarimarco.uniroma2.is.service.persistence;

import info.ferrarimarco.uniroma2.is.model.ProductInstance;

public interface ProductInstancePersistenceService extends EntityPersistenceService<ProductInstance>{
    Long countByProductId(String productId);
    Long countInstancesByProductId(String productId);
}
