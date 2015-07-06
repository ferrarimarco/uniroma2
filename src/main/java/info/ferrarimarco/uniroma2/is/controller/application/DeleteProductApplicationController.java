package info.ferrarimarco.uniroma2.is.controller.application;

import lombok.extern.slf4j.Slf4j;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

@Slf4j
public class DeleteProductApplicationController extends AbstractApplicationController {

    public DeleteProductApplicationController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService, StatService statService) {
        super(productPersistenceService, productInstancePersistenceService, null, null, statService);
    }

    public void deleteExistingProduct(String id) {
        productInstancePersistenceService.deleteByProductId(id);
        productPersistenceService.delete(id);
        statService.deleteByEntityId(id);
        log.debug("Deleted product {}", id);
    }

}
