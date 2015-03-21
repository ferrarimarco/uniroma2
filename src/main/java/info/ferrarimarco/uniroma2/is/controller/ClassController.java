package info.ferrarimarco.uniroma2.is.controller;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.service.persistence.InstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.PersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private PersistenceService<Clazz> classService;
    
    @Autowired
    private InstancePersistenceService instanceService;

    @ModelAttribute("allClasses")
    public List<Clazz> populateClasses() {
        List<Clazz> allClasses = classService.findAll();
        
        for(Clazz clazz : allClasses){
            clazz.setInstanceCount(instanceService.findInstanceCountByInstanceClass(clazz));
        }
        
        return allClasses;
    }

    @RequestMapping(value = {"/"})
    public String index() {
        return "class.html";
    }

}
