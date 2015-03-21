package info.ferrarimarco.uniroma2.is.service.persistence;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Instance;

public interface InstancePersistenceService extends PersistenceService<Instance> {
    List<Instance> findByInstanceClass(Clazz instanceClazz);
    int findInstanceCountByInstanceClass(Clazz instanceClazz);
}
