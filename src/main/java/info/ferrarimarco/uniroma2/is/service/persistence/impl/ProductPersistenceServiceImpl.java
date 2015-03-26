package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ProductRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import java.util.List;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductPersistenceServiceImpl extends EntityPersistenceServiceImpl<Product> implements ProductPersistenceService {
    
    @Autowired
    @Getter
    private ProductRepository repository;

    @Override
    public List<Product> findByClazz(Clazz clazz) {
        return getRepository().findByClazz(clazz);
    }

    @Override
    public Page<Product> findByClazz(Clazz clazz, Pageable pageable) {
        return getRepository().findByClazz(clazz, pageable);
    }

    @Override
    protected EntityRepository<Product> getEntityRepository() {
        return this.getRepository();
    }
}
