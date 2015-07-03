package info.ferrarimarco.uniroma2.is.service.persistence;

import info.ferrarimarco.uniroma2.is.model.EntityStat;

public interface EntityStatPersistenceService extends EntityPersistenceService<EntityStat>{
    void saveAsync(EntityStat entity);
}
