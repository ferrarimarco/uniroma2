package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.ProductInstance;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ProductInstanceRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class ProductInstancePersistenceServiceImpl extends EntityPersistenceServiceImpl<ProductInstance> implements ProductInstancePersistenceService {
    
    @Data
    class ProductInstanceSumAggregation{
        private String productId;
        private Long sum;
    }
    
    @Autowired
    private ProductPersistenceService productPersistenceService;

    @Autowired
    @Getter
    private ProductInstanceRepository repository;

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
    public Map<String,Long> deleteExpired() {
        Page<Product> productPage;
        int pageNumber = -1;
        
        Map<String,Long> result = new HashMap<>();

        do{
            productPage = productPersistenceService.findAll(new PageRequest(++pageNumber, 30));
            
            DateTime now = new DateTime();
            
            Aggregation aggregation = newAggregation(
                    match(Criteria.where("expirationDate").lt(now)),
                    group("productId").sum("amount").as("sum"),
                    project("sum").and("productId").previousOperation()
                    );
            AggregationResults<ProductInstanceSumAggregation> groupResults = mongoTemplate.aggregate(aggregation, ProductInstance.class, ProductInstanceSumAggregation.class);
            
            for(ProductInstanceSumAggregation productInstanceSumAggregation : groupResults.getMappedResults()){
                Product p = productPersistenceService.findById(productInstanceSumAggregation.getProductId());
                result.put(p.getId(), productInstanceSumAggregation.getSum());
                Query removeQuery = new Query();
                removeQuery.addCriteria(Criteria.where("productId").is(productInstanceSumAggregation.getProductId()).and("expirationDate").lt(now));
                mongoTemplate.remove(removeQuery, ProductInstance.class);
            }
            
        }while (!productPage.isLast());
        
        return result;
    }

}
