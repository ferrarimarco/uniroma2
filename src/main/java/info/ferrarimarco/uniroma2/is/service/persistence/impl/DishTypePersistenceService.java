package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;

import info.ferrarimarco.uniroma2.is.model.DishType;
import info.ferrarimarco.uniroma2.is.persistence.repositories.DishTypeRepository;

public class DishTypePersistenceService extends AbstractPersistenceService<DishType> {

    @Autowired
    @Getter
    private DishTypeRepository repository;
    
    @Override
    public DishType findById(String id) {
        return repository.findOne(id);
    }
}
