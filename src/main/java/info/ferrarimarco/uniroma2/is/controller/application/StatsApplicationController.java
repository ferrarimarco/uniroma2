package info.ferrarimarco.uniroma2.is.controller.application;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.util.StatResult;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import org.apache.commons.lang.exception.ExceptionUtils;

@Slf4j
@AllArgsConstructor
public class StatsApplicationController {
    
    @NonNull
    private StatService statService;
    
    @NonNull
    private ProductPersistenceService productPersistenceService;
    
    @NonNull
    private ProductInstancePersistenceService productInstancePersistenceService;

    @NonNull
    private ClazzPersistenceService clazzPersistenceService;
    
    @NonNull
    private CategoryPersistenceService categoryPersistenceService;
    
    public StatResult computeIndex(String indexType, String criteriaId, Class<? extends Entity> criteriaClass){
        double result = 0.0;
        try{
            switch(indexType){
            case Constants.SUCCESS_INDEX:
                result = statService.success(criteriaId, criteriaClass);
                break;
            case Constants.LIKING_INDEX:
                result = statService.liking(criteriaId, criteriaClass);
                break;
            case Constants.PERISHABILITY_INDEX:
                result = statService.perishability(criteriaId, criteriaClass);
                break;
            default:
                throw new IllegalArgumentException("Cannot choose a index for: " + indexType);
            }
        }catch(ArithmeticException e){
            log.warn("Cannot compute {} index: {}", indexType, ExceptionUtils.getStackTrace(e));
        }
        
        Entity entity = loadEntity(criteriaId, criteriaClass); 
        
        return new StatResult(entity, result);
    }
    
    public List<Product> loadAllProducts(){
        return productPersistenceService.findAll();
    }
    
    private Entity loadEntity(String criteriaId, Class<? extends Entity> criteriaClass){
        if(Category.class.equals(criteriaClass))
            return categoryPersistenceService.findById(criteriaId);
        else if(Clazz.class.equals(criteriaClass))
            return clazzPersistenceService.findById(criteriaId);
        else if(Product.class.equals(criteriaClass))
            return productPersistenceService.findById(criteriaId);
        else
            throw new IllegalArgumentException("Cannot load entity. Not handled: " + criteriaClass.toString());
    }
}
