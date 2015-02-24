package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.is.model.Dish;
import info.ferrarimarco.uniroma2.is.persistence.repositories.DishRepository;

@Service
public class DishPersistenceService extends AbstractPersistenceService<Dish> {
    
    @Autowired
    @Getter
    private DishRepository repository;
    
    @Override
    public Dish findById(String id) {
        return repository.findOne(id);
    }
}
