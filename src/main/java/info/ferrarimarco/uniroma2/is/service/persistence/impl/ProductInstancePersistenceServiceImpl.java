package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.ProductInstance;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ClazzRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ProductInstanceRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import lombok.Getter;
import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductInstancePersistenceServiceImpl extends EntityPersistenceServiceImpl<ProductInstance> implements ProductInstancePersistenceService {
    
    @Autowired
    @Getter
    private ProductInstanceRepository repository;
    
    @Autowired
    private ClazzRepository classRepository;
    
    @Override
    protected EntityRepository<ProductInstance> getEntityRepository() {
        return this.getRepository();
    }
    
    public ProductInstance save(@NonNull ProductInstance product){
        product.setSymbolicId(Constants.PRODUCT_INSTANCE_SYM_ID_PREFIX + counterService.getNextProductEntitySequence());
        return super.save(product);
    }
    
    public Long countByProductId(String productId){
        return getRepository().countByProductId(productId);
    }

    @Override
    public Long countInstancesByProductId(String productId) {
        List<ProductInstance> instances = getRepository().findByProductId(productId);
        Long count = 0L;
        
        for(ProductInstance instance : instances){
            count += instance.getAmount();
        }
        return count;
    }

}
