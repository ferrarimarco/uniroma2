package info.ferrarimarco.uniroma2.is.controller.application;

import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractApplicationController {
    protected ProductPersistenceService productPersistenceService;
    protected ProductInstancePersistenceService productInstancePersistenceService;
    protected ClazzPersistenceService clazzPersistenceService;
    protected CategoryPersistenceService categoryPersistenceService;
    protected StatService statService;
}
