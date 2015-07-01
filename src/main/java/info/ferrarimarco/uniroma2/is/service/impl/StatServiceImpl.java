package info.ferrarimarco.uniroma2.is.service.impl;

import info.ferrarimarco.uniroma2.is.model.EntityStat;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.EntityStatPersistenceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatServiceImpl implements StatService {

    private enum ProductStat{
        REQUESTED,
        DISPENSED,
        EXPIRED,
        STOCKED
    }

    private Map<String,EntityStat> stats;

    @Autowired
    private EntityStatPersistenceService entityStatPersistenceService;

    public StatServiceImpl() {
        stats = new HashMap<>();
    }

    @PostConstruct
    private void init(){
        log.debug("Loading entityStats");
        List<EntityStat> entityStats = entityStatPersistenceService.findAll(); 
        for(EntityStat entityStat : entityStats)
            stats.put(entityStat.getEntityId(), entityStat);
    }

    @PreDestroy
    private void tearDown(){
        log.debug("Saving entityStats");
        for(String entityId : stats.keySet())
            entityStatPersistenceService.save(stats.get(entityId));
    }

    @Override
    public void initProductStat(String productId, String clazzId, String categoryId){
        // Init product stat
        if(!stats.containsKey(productId))
            stats.put(productId, 
                    EntityStat.builder()
                    .entityId(productId)
                    .parentEntityId(clazzId)
                    .dispensed(0L)
                    .expired(0L)
                    .requested(0L)
                    .stocked(0L)
                    .build()
                    );
        else
            log.warn("Stat for {} has already been initialized");

        // Init clazz stat if necessary
        if(!stats.containsKey(clazzId))
            stats.put(clazzId, 
                    EntityStat.builder()
                    .entityId(clazzId)
                    .parentEntityId(categoryId)
                    .dispensed(0L)
                    .expired(0L)
                    .requested(0L)
                    .stocked(0L)
                    .build()
                    );

        // Init category stat if necessary
        if(!stats.containsKey(categoryId))
            stats.put(categoryId, 
                    EntityStat.builder()
                    .entityId(categoryId)
                    .parentEntityId(null)
                    .dispensed(0L)
                    .expired(0L)
                    .requested(0L)
                    .stocked(0L)
                    .build()
                    );
    }

    public void clearStats(){
        stats.clear();
    }

    private Long computeTotal(ProductStat productStat){
        Long total = 0L;
        for(String entityId : stats.keySet()){
            // Get only categories
            if(stats.get(entityId).getParentEntityId() == null)
                total += getStatByCriteria(entityId, productStat);
        }
        return total;
    }

    private Long getStatByCriteria(String criteriaId, ProductStat productStat){
        switch(productStat){
        case DISPENSED:
            return stats.get(criteriaId).getDispensed();
        case EXPIRED:
            return stats.get(criteriaId).getExpired();
        case REQUESTED:
            return stats.get(criteriaId).getRequested();
        case STOCKED:
            return stats.get(criteriaId).getStocked();
        default:
            throw new IllegalArgumentException("Cannot select stat");
        }
    }

    @Override
    public Double success(String entityId) {
        Long totalDispensed = getStatByCriteria(entityId, ProductStat.DISPENSED);
        Long totalRequested = getStatByCriteria(entityId, ProductStat.REQUESTED);

        if(totalRequested <= 0)
            throw new ArithmeticException ("Cannot compute success stat. This item has not been requested yet.");

        return totalDispensed.doubleValue()/totalRequested.doubleValue();
    }

    @Override
    public Double perishability(String entityId) {
        Long totalExpired = getStatByCriteria(entityId, ProductStat.EXPIRED);
        Long totalStocked = getStatByCriteria(entityId, ProductStat.STOCKED);

        if(totalStocked <= 0)
            throw new ArithmeticException ("Cannot compute perishability stat. This item has not been stocked yet.");

        return totalExpired.doubleValue()/totalStocked.doubleValue();
    }

    @Override
    public Double liking(String entityId) {
        Long dispensed = getStatByCriteria(entityId, ProductStat.DISPENSED);
        Long totalDispensed = 0L;
        EntityStat entityStat = stats.get(entityId);
        if(StringUtils.isBlank(entityStat.getParentEntityId()))
            // Get all the products
            totalDispensed = computeTotal(ProductStat.DISPENSED);
        else
            totalDispensed = getStatByCriteria(entityStat.getParentEntityId(), ProductStat.DISPENSED);

        if(totalDispensed <= 0)
            throw new ArithmeticException ("Cannot compute liking stat. This item has not been dispensed yet.");

        return dispensed.doubleValue()/totalDispensed.doubleValue();
    }

    private void addStatForProduct(String productId, Long value, ProductStat productStatType){
        EntityStat productStat = stats.get(productId);
        EntityStat clazzStat = stats.get(productStat.getParentEntityId());
        EntityStat categoryStat = stats.get(clazzStat.getParentEntityId());
        switch(productStatType){
        case DISPENSED:
            stats.get(productStat.getEntityId()).setDispensed(productStat.getDispensed() + value);
            stats.get(clazzStat.getEntityId()).setDispensed(clazzStat.getDispensed() + value);
            stats.get(categoryStat.getEntityId()).setDispensed(categoryStat.getDispensed() + value);
            break;
        case EXPIRED:
            stats.get(productStat.getEntityId()).setExpired(productStat.getExpired() + value);
            stats.get(clazzStat.getEntityId()).setExpired(clazzStat.getExpired() + value);
            stats.get(categoryStat.getEntityId()).setExpired(categoryStat.getExpired() + value);
            break;
        case REQUESTED:
            stats.get(productStat.getEntityId()).setRequested(productStat.getRequested() + value);
            stats.get(clazzStat.getEntityId()).setRequested(clazzStat.getRequested() + value);
            stats.get(categoryStat.getEntityId()).setRequested(categoryStat.getRequested() + value);
            break;
        case STOCKED:
            stats.get(productStat.getEntityId()).setStocked(productStat.getStocked() + value);
            stats.get(clazzStat.getEntityId()).setStocked(clazzStat.getStocked() + value);
            stats.get(categoryStat.getEntityId()).setStocked(categoryStat.getStocked() + value);
            break;
        }
    }

    @Override
    public void addRequested(String productId, Long value) {
        addStatForProduct(productId, value, ProductStat.REQUESTED);
    }

    @Override
    public void addDispensed(String productId, Long value) {
        addStatForProduct(productId, value, ProductStat.DISPENSED);
    }

    @Override
    public void addExpired(String productId, Long value) {
        addStatForProduct(productId, value, ProductStat.EXPIRED);
    }

    @Override
    public void addStocked(String productId, Long value) {
        addStatForProduct(productId, value, ProductStat.STOCKED);
    }

    @Override
    public Long getRequested(String productId) {
        return stats.get(productId).getRequested();
    }

    @Override
    public Long getDispensed(String productId) {
        return stats.get(productId).getDispensed();
    }

    @Override
    public Long getExpired(String productId) {
        return stats.get(productId).getExpired();
    }

    @Override
    public Long getStocked(String productId) {
        return stats.get(productId).getStocked();
    }
}
