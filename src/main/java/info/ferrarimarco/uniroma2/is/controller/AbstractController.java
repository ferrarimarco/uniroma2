package info.ferrarimarco.uniroma2.is.controller;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public class AbstractController {
    @Autowired
    protected ProductPersistenceService productPersistenceService;
    
    @Autowired
    protected ProductInstancePersistenceService productInstancePersistenceService;

    @Autowired
    protected ClazzPersistenceService clazzPersistenceService;
    
    @ModelAttribute("allClasses")
    protected List<Clazz> getAllClasses(){
        return clazzPersistenceService.findAll();
    }
}
