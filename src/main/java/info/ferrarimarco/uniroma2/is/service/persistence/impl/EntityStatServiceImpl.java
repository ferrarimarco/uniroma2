package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.EntityStat;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityRepository;
import info.ferrarimarco.uniroma2.is.persistence.repositories.EntityStatRepository;
import info.ferrarimarco.uniroma2.is.service.persistence.EntityStatPersistenceService;
import lombok.Getter;
import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EntityStatServiceImpl extends EntityPersistenceServiceImpl<EntityStat> implements EntityStatPersistenceService {
    
    @Autowired
    @Getter
    private EntityStatRepository repository;

    @Override
    protected EntityRepository<EntityStat> getEntityRepository() {
        return this.getRepository();
    }
    
    @Override
    public EntityStat save(@NonNull EntityStat entityStat){
        entityStat.setSymbolicId(Constants.ENTITY_STAT_COUNTER_NAME + counterService.getNextEntityStatSequence());
        return super.save(entityStat);
    }
    
    @Override
    @Async
    public void saveAsync(EntityStat entity){
        this.save(entity);
    }

    @Override
    @Async
    public void deleteAsync(String id) {
        this.delete(id);
    }
}
