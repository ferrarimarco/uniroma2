package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.is.model.Instance;
import info.ferrarimarco.uniroma2.is.persistence.repositories.InstanceRepository;

@Service
public class InstancePersistenceService extends AbstractPersistenceService<Instance> {
    
    @Autowired
    @Getter
    private InstanceRepository repository;
    
    @Override
    public Instance findById(String id) {
        return repository.findOne(id);
    }
}
