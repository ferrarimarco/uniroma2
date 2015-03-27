package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.CounterService;
import info.ferrarimarco.uniroma2.is.service.persistence.EntityPersistenceService;

public abstract class EntityPersistenceServiceImpl<T extends Entity> implements EntityPersistenceService<T>{
    
    @Autowired
    protected MongoTemplate mongoTemplate;
    
    @Autowired
    protected CounterService counterService;

    protected abstract MongoRepository<T, String> getRepository();
    
    /**
     * This should return the same object returned by getRepository.
     */
    protected abstract EntityRepository<T> getEntityRepository();
    
    @Override
    public void deleteAll() {
        getRepository().deleteAll();
    }

    @Override
    public List<T> findAll(){
        return getRepository().findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable){
        return getRepository().findAll(pageable);
    }

    @Override
    public T findById(String id) {
        return getRepository().findOne(id);
    }
    
    @Override
    public List<T> findByName(String name) {
        return getEntityRepository().findByName(name);
    }

    @Override
    public Page<T> findByName(String name, Pageable pageable) {
        return getEntityRepository().findByName(name, pageable);
    }
    
    @Override
    public T save(T entity){
        return getRepository().save(entity);
    }
}
