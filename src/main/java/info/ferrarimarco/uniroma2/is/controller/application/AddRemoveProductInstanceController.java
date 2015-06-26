package info.ferrarimarco.uniroma2.is.controller.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import lombok.extern.slf4j.Slf4j;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.ProductInstance;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

@Slf4j
public class AddRemoveProductInstanceController extends AbstractApplicationController {

    public AddRemoveProductInstanceController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService,
            ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService) {
        super(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService);
    }
    
    public void addProductInstance(ProductInstance productInstance, Long newAmount){
        Product product = loadProduct(productInstance.getId());
        productInstancePersistenceService.save(productInstance);
        product.setStocked(product.getStocked() + newAmount);
        productPersistenceService.save(product);
        log.info("Added {} instances for {}", newAmount, product);
    }
    
    public void removeProductInstance(ProductInstance productInstance, Long newAmount){
        Product product = loadProduct(productInstance.getId());
        log.info("Removing {} instances for {}", newAmount, product);
        Long count = productInstancePersistenceService.countInstancesByProductId(productInstance.getProductId());
        
        if(count > newAmount){
            product.setRequested(product.getRequested() + newAmount);
            product.setDispensed(product.getDispensed() + newAmount);
            
            Page<ProductInstance> productInstances = null;
            int pageIndex = -1;
            do{
                productInstances = productInstancePersistenceService.findByProductId(product.getId(), new PageRequest(++pageIndex, 10));
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
        }else{
            product.setRequested(product.getRequested() + (newAmount - count));
        }
        productPersistenceService.save(product);
    }
    
    public void removeExpiredProductInstances(){
        productInstancePersistenceService.deleteExpired();
        log.info("Removed expired product instances");
    }
    
    private Product loadProduct(String productId){
        Product product = productPersistenceService.findById(productId);
        
        if(product == null)
            throw new NullPointerException("product");
        
        return product;
    }

}
