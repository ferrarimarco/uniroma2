package info.ferrarimarco.uniroma2.is.config.initializer.persistence;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.CounterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MongoDbInitializer implements ApplicationListener<ContextRefreshedEvent>{
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private CategoryPersistenceService categoryPersistenceService;
    
    @Autowired
    private ClazzPersistenceService classPersistenceService;
    
    @Autowired
    private CounterService counterService;
    
    @Value("${config.persistence.forceinitialization}")
    private boolean forceInitialization;
    
    @Value("#{'${data.init.categories}'.split(',')}")
    private List<String> categories;
    
    @Value("${data.init.classes}")
    private String rawClasses;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(forceInitialization){
            mongoTemplate.dropCollection(Product.class);
            mongoTemplate.dropCollection(Clazz.class);
            mongoTemplate.dropCollection(Category.class);
        }
        
        // Initialise categories
        if(!mongoTemplate.collectionExists(Category.class)){
            for(String category : categories){
                Category c = new Category();
                c.setName(category);
                c.setSymbolicId(Constants.CATEGORY_SYM_ID_PREFIX + counterService.getNextCategorySequence());
                categoryPersistenceService.save(c);
            }
        }
        
        // Initialise categories
        if(!mongoTemplate.collectionExists(Clazz.class)){
            String[] classCategoryTuples = rawClasses.split(";");
            
            for(String clazzCategoryTuple : classCategoryTuples){
                try{
                    String[] clazzData = clazzCategoryTuple.split(":");
                    Clazz c = new Clazz();
                    c.setName(clazzData[0]);
                    c.setCategory(categoryPersistenceService.findByName(clazzData[1]).get(0));
                    c.setSymbolicId(Constants.CLASS_SYM_ID_PREFIX + counterService.getNextClazzSequence());
                    classPersistenceService.save(c);                    
                }catch(Exception e){
                    log.error("Error while initializing db with class {}: {}", clazzCategoryTuple);
                }

            }
        }
    }

}
