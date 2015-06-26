package info.ferrarimarco.uniroma2.is.controller.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

public class LoadEntityApplicationController extends AbstractApplicationController{
    
    public List<Clazz> getAllClasses(){
        return clazzPersistenceService.findAll();
    }
    
    public List<Category> getAllCategories(){
        return categoryPersistenceService.findAll();
    }
    
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
