package info.ferrarimarco.uniroma2.is.controller.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

public class LoadEntityApplicationController extends AbstractApplicationController{

    public LoadEntityApplicationController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService,
            ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService) {
        super(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService);
    }
    
    public Page<Product> loadProductPage(Pageable pageable){
        Page<Product> allEntitesPage = productPersistenceService.findAll(pageable);
        if(allEntitesPage != null)
            for(Product product : allEntitesPage){
                product.setAmount(productInstancePersistenceService.countInstancesByProductId(product.getId()));
            }
        
        return allEntitesPage;
    }
    
    public Product loadProduct(String id){
        return productPersistenceService.findById(id);
    }

}
