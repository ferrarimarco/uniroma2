package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ClazzRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;

import java.util.List;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClazzPersistenceServiceImpl extends EntityPersistenceServiceImpl<Clazz> implements ClazzPersistenceService {

    @Autowired
    @Getter
    private ClazzRepository repository;
    
    @Override
    public List<Clazz> findByCategory(Category category) {
        return getRepository().findByCategory(category);
    }

    @Override
    public Page<Clazz> findByCategory(Category category, Pageable pageable) {
        return getRepository().findByCategory(category, pageable);
    }

    @Override
    protected EntityRepository<Clazz> getEntityRepository() {
        return this.getRepository();
    }

}
