package info.ferrarimarco.uniroma2.is.service.persistence;

import info.ferrarimarco.uniroma2.is.model.Entity;

public interface PersistenceService<T extends Entity> {
    void deleteAll();
    T findById(String id);
}
