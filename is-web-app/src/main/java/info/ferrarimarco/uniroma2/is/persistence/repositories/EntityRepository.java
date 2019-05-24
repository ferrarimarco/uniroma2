package info.ferrarimarco.uniroma2.is.persistence.repositories;

import info.ferrarimarco.uniroma2.is.model.Entity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityRepository<T extends Entity> {
    List<T> findByName(String name);
    Page<T> findByName(String name, Pageable pageable);
}
