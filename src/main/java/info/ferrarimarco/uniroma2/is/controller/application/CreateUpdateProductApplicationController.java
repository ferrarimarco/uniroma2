package info.ferrarimarco.uniroma2.is.controller.application;

import org.apache.commons.lang.StringUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

@Slf4j
public class CreateUpdateProductApplicationController extends AbstractApplicationController {

    public CreateUpdateProductApplicationController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService,
            ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService, StatService statService) {
        super(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService, statService);
    }
    
    public void createNewProduct(@NonNull String clazzId, @NonNull Product product){
        if (StringUtils.isBlank(clazzId))
            throw new IllegalArgumentException("Product class cannot be null");
            
        product.setId(null);
        saveProduct(clazzId, product);
        log.info("Created new product: {}", product.toString());
    }
    
    public void updateExistingProduct(@NonNull String clazzId, @NonNull Product product){
        if(StringUtils.isBlank(product.getId()) || !productPersistenceService.exists(product.getId())){
            log.warn("This product does not exist. A creation will be performed.");
            createNewProduct(clazzId, product);
        }else{
            saveProduct(clazzId, product);
            log.info("Updated product: {}", product.toString());
        }
    }
    
    private void saveProduct(String clazzId, Product product){
        product.setClazz(clazzPersistenceService.findById(clazzId));
        product.setCategory(product.getClazz().getCategory());
        Product p = productPersistenceService.save(product);
        log.info("Saved product: {}", p);
        statService.initProductStat(product.getId(), product.getClazz().getId(), product.getCategory().getId());
    }
}
