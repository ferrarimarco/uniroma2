package info.ferrarimarco.uniroma2.is.service.impl;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class StatServiceImpl implements StatService {

    private enum ProductStat{
        REQUESTED,
        DISPENSED,
        EXPIRED,
        STOCKED
    }

    @Autowired
    private CategoryPersistenceService categoryPersistenceService;

    @Autowired
    private ClazzPersistenceService clazzPersistenceService;

    @Autowired
    private ProductPersistenceService productPersistenceService;

    private Long computeTotal(Class<? extends Entity> criteriaClass, ProductStat productStat){
        int pageIndex = -1;
        Long total = 0L;

        Page<Product> products = null;
        do{
            if(Category.class.equals(criteriaClass)){
                products = productPersistenceService.findAll(new PageRequest(++pageIndex, 10));
            }else if(Clazz.class.equals(criteriaClass)){
                products = productPersistenceService.findAll(new PageRequest(++pageIndex, 10));
            }else{
                throw new IllegalArgumentException("Entity class not handled");
            }

            for(Product product : products){
                switch(productStat){
                case DISPENSED:
                    total += product.getDispensed();
                    break;
                case EXPIRED:
                    total += product.getExpired();
                    break;
                case REQUESTED:
                    total += product.getRequested();
                    break;
                case STOCKED:
                    total += product.getStocked();
                    break;
                }
            }
        }while(products != null && products.hasNext());

        return total;        
    }

    private Long computeTotalByCriteria(String criteriaId, Class<? extends Entity> criteriaClass, ProductStat productStat){
        int pageIndex = -1;
        Long total = 0L;
        Entity criteria = null;
        if(Category.class.equals(criteriaClass)){
            criteria = categoryPersistenceService.findById(criteriaId);
        }else if(Clazz.class.equals(criteriaClass)){
            criteria = clazzPersistenceService.findById(criteriaId);
        }else{
            throw new IllegalArgumentException("Entity class not handled");
        }

        if(criteria == null){
            throw new NullPointerException("criteria cannot be null");
        }

        Page<Product> products = null;
        do{
            if(Category.class.equals(criteriaClass)){
                products = productPersistenceService.findByCategory((Category) criteria, new PageRequest(++pageIndex, 10));
            }else if(Clazz.class.equals(criteriaClass)){
                products = productPersistenceService.findByClazz((Clazz) criteria, new PageRequest(++pageIndex, 10));
            }else{
                throw new IllegalArgumentException("Entity class not handled");
            }

            for(Product product : products){
                switch(productStat){
                case DISPENSED:
                    total += product.getDispensed();
                    break;
                case EXPIRED:
                    total += product.getExpired();
                    break;
                case REQUESTED:
                    total += product.getRequested();
                    break;
                case STOCKED:
                    total += product.getStocked();
                    break;
                }
            }
        }while(products != null && products.hasNext());

        return total;        
    }

    @Override
    public Double success(String entityId, Class<? extends Entity> clazz) {
        Long totalDispensed = 0L;
        Long totalRequested = 0L;

        if(Category.class.equals(clazz) || Clazz.class.equals(clazz)){
            totalDispensed = computeTotalByCriteria(entityId, clazz, ProductStat.DISPENSED);
            totalRequested = computeTotalByCriteria(entityId, clazz, ProductStat.REQUESTED);
        }else if(Product.class.equals(clazz)){
            Product product = productPersistenceService.findById(entityId);
            totalDispensed = product.getDispensed();
            totalRequested = product.getRequested();
        }else{
            throw new IllegalArgumentException("Entity class not handled");
        }

        if(totalRequested <= 0){
            throw new ArithmeticException ("Cannot compute success stat. This item has not been requested yet.");
        }

        return totalDispensed.doubleValue()/totalRequested.doubleValue();
    }
    
    @Override
    public Double perishability(String entityId, Class<? extends Entity> clazz) {
        Long totalExpired = 0L;
        Long totalStocked = 0L;

        if(Category.class.equals(clazz) || Clazz.class.equals(clazz)){
            totalExpired = computeTotalByCriteria(entityId, clazz, ProductStat.EXPIRED);
            totalStocked = computeTotalByCriteria(entityId, clazz, ProductStat.STOCKED);
        }else if(Product.class.equals(clazz)){
            Product product = productPersistenceService.findById(entityId);
            totalExpired = product.getExpired();
            totalStocked = product.getRequested();
        }else{
            throw new IllegalArgumentException("Entity class not handled");
        }

        if(totalStocked <= 0){
            throw new ArithmeticException ("Cannot compute success stat. This item has not been requested yet.");
        }

        return totalExpired.doubleValue()/totalStocked.doubleValue();
    }

    @Override
    public Double liking(String entityId, Class<? extends Entity> clazz) {
        Long dispensed = 0L;
        Long totalDispensed = 0L;

        if(Category.class.equals(clazz)){
            dispensed = computeTotalByCriteria(entityId, clazz, ProductStat.DISPENSED);
            totalDispensed = computeTotal(Category.class, ProductStat.DISPENSED);
        }else if(Clazz.class.equals(clazz)){
            dispensed = computeTotalByCriteria(entityId, clazz, ProductStat.DISPENSED);
            Clazz productClazz = clazzPersistenceService.findById(entityId);
            totalDispensed = computeTotalByCriteria(productClazz.getCategory().getId(), Category.class, ProductStat.DISPENSED); 
        }else if(Product.class.equals(clazz)){
            Product product = productPersistenceService.findById(entityId);
            dispensed = product.getDispensed();
            totalDispensed = computeTotalByCriteria(product.getClazz().getId(), Clazz.class, ProductStat.DISPENSED);
        }else{
            throw new IllegalArgumentException("Entity class not handled");
        }

        if(totalDispensed <= 0){
            throw new ArithmeticException ("Cannot compute success stat. This item has not been requested yet.");
        }

        return dispensed.doubleValue()/totalDispensed.doubleValue();
    }
}
