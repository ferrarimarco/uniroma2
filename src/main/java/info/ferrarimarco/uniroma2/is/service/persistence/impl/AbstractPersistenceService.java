package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.service.persistence.PersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public abstract class AbstractPersistenceService<T extends Entity> implements PersistenceService<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;
    
    protected abstract MongoRepository<T, String> getRepository();
    
    @Override
    public void deleteAll() {
        getRepository().deleteAll();
    }
    
    public List<T> findAll(){
        return getRepository().findAll();
    }
    
    public Page<T> findAll(Pageable pageable){
        return getRepository().findAll(pageable);
    }
}
