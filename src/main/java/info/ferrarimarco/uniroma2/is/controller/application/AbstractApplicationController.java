package info.ferrarimarco.uniroma2.is.controller.application;

import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public abstract class AbstractApplicationController {
    @NonNull
    protected ProductPersistenceService productPersistenceService;
    
    @NonNull
    protected ProductInstancePersistenceService productInstancePersistenceService;

    @NonNull
    protected ClazzPersistenceService clazzPersistenceService;
    
    @NonNull
    protected CategoryPersistenceService categoryPersistenceService;
}
