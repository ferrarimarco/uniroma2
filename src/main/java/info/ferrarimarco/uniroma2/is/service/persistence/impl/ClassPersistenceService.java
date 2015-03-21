package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;

import info.ferrarimarco.uniroma2.is.model.Class;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ClassRepository;

public class ClassPersistenceService extends AbstractPersistenceService<Class> {

    @Autowired
    @Getter
    private ClassRepository repository;
    
    @Override
    public Class findById(String id) {
        return repository.findOne(id);
    }
}
