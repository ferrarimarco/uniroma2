package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.persistence.repositories.CategoryRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryPersistenceServiceImpl extends EntityPersistenceServiceImpl<Category> implements CategoryPersistenceService {

    @Autowired
    @Getter
    private CategoryRepository repository;

    @Override
    protected EntityRepository<Category> getEntityRepository() {
        return this.getRepository();
    }
}
