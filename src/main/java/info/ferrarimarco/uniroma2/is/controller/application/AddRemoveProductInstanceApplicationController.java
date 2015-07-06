package info.ferrarimarco.uniroma2.is.controller.application;

import info.ferrarimarco.uniroma2.is.model.ProductInstance;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Slf4j
public class AddRemoveProductInstanceApplicationController extends AbstractApplicationController {
    
    public AddRemoveProductInstanceApplicationController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService,
            ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService, StatService statService) {
        super(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService, statService);
    }
    
    public void addProductInstance(ProductInstance productInstance, Long newAmount){
        productInstancePersistenceService.save(productInstance);
        statService.addStocked(productInstance.getProductId(), newAmount);
        log.info("Added {} instances for {}", newAmount, productInstance.getProductId());
    }
    
    public void removeProductInstance(ProductInstance productInstance, Long newAmount){
        log.info("Removing {} instances for {}", newAmount, productInstance.getProductId());
        Long count = productInstancePersistenceService.countInstancesByProductId(productInstance.getProductId());
        if(count > newAmount){
            log.info("Removed {} instances for {}", newAmount, productInstance.getProductId());
            statService.addRequested(productInstance.getProductId(), newAmount);
            statService.addDispensed(productInstance.getProductId(), newAmount);
            
            Page<ProductInstance> productInstances = null;
            int pageIndex = -1;
            do{
                productInstances = productInstancePersistenceService.findByProductId(productInstance.getProductId(), new PageRequest(++pageIndex, 10));
                for(int i = 0; i < productInstances.getContent().size() && newAmount > 0; i++){
                    ProductInstance pi = productInstances.getContent().get(i);
                    if(pi.getAmount() <= newAmount){
                        newAmount = newAmount - pi.getAmount();
                        productInstancePersistenceService.delete(pi.getId());
                    }else{
                        pi.setAmount(pi.getAmount() - newAmount);
                        productInstancePersistenceService.save(pi);
                        newAmount = 0L;
                    }
                }
            }
            while(newAmount > 0);
        }else
            statService.addRequested(productInstance.getProductId(), newAmount - count);
    }
    
    public void removeExpiredProductInstances(){
        Map<String,Long> expiredProductIdToQuantity = productInstancePersistenceService.deleteExpired();
        for(String productId : expiredProductIdToQuantity.keySet()){
            statService.addExpired(productId, expiredProductIdToQuantity.get(productId));
            log.info("Removed {} expired product instances for {}", expiredProductIdToQuantity.keySet(), productId);
        }
    }
}
