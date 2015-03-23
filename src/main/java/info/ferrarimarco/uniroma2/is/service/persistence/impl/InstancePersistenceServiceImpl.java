package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import java.util.List;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Instance;
import info.ferrarimarco.uniroma2.is.persistence.repositories.InstanceRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.InstancePersistenceService;

@Service
public class InstancePersistenceServiceImpl extends AbstractPersistenceService<Instance> implements InstancePersistenceService {
    
    @Autowired
    @Getter
    private InstanceRepository repository;
    
    @Override
    public Instance findById(String id) {
        return getRepository().findOne(id);
    }

    @Override
    public List<Instance> findByInstanceClass(Clazz instanceClazz) {
        return getRepository().findByInstanceClass(instanceClazz);
    }

    @Override
    public int findInstanceCountByInstanceClass(Clazz instanceClazz) {
        return getRepository().findByInstanceClass(instanceClazz).size();
    }
}
