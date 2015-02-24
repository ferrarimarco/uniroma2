package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.service.persistence.PersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
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
}
