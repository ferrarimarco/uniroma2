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

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;

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
    
    @Override
    public ProductInstance save(@NonNull ProductInstance product){
        product.setSymbolicId(Constants.PRODUCT_INSTANCE_SYM_ID_PREFIX + counterService.getNextProductEntitySequence());
        return super.save(product);
    }
    
    @Override
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

    @Override
    public Page<ProductInstance> findByProductId(String productId, Pageable pageable) {
        return getRepository().findByProductId(productId, pageable);
    }

    @Override
    public Long deleteExpired() {
        Query query = new Query();
        query.addCriteria(Criteria.where("expirationDate").lt(new DateTime()));
        mongoTemplate.remove(query, ProductInstance.class);
        return null;
    }

}
