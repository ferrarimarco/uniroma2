package info.ferrarimarco.uniroma2.is.service.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import info.ferrarimarco.uniroma2.is.model.Entity;

public interface PersistenceService<T extends Entity> {
    void deleteAll();
    T findById(String id);
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
}
