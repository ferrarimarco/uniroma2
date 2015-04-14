package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.ProductInstance;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ClazzRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ProductInstanceRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ProductRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
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

import com.mongodb.WriteResult;


//imports as static
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Service
public class ProductInstancePersistenceServiceImpl extends EntityPersistenceServiceImpl<ProductInstance> implements ProductInstancePersistenceService {

    @Autowired
    private ProductRepository productRepository;

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
    public void deleteExpired() {
        Page<Product> productPage;
        int pageNumber = -1;

        do{
            productPage = productRepository.findAll(new PageRequest(++pageNumber, 30));

            Aggregation aggregation = newAggregation(
                    match(Criteria.where("expirationDate").lt(new DateTime())),
                    group("productId").sum("amount").as("expiredSum"),
                    project("productId").and("expiredSum")
                    );
            AggregationResults<Object> groupResults = mongoTemplate.aggregate(aggregation, ProductInstance.class, Object.class);
            List<Object> result = groupResults.getMappedResults();
            
        }while (!productPage.isLast());
    }

}
