package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.persistence.repositories.ClassRepository;

@Service
public class ClassPersistenceServiceImpl extends AbstractPersistenceService<Clazz> {

    @Autowired
    @Getter
    private ClassRepository repository;
    
    @Override
    public Clazz findById(String id) {
        return getRepository().findOne(id);
    }
}
