package info.ferrarimarco.uniroma2.is.controller;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.service.persistence.InstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.PersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private PersistenceService<Clazz> classService;
    
    @Autowired
    private InstancePersistenceService instanceService;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Pageable pageable) {
        
        Page<Clazz> allClassesPage = classService.findAll(pageable);
        
        for(Clazz clazz : allClassesPage){
            clazz.setInstanceCount(instanceService.findInstanceCountByInstanceClass(clazz));
        }
        
        model.addAttribute("allClassesPage", allClassesPage);
        
        return "class.html";
    }
    
    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public String createClass() {
        return "class.html";
    }
    
    @RequestMapping(value = {"", "/{classId}"}, method = RequestMethod.PUT)
    public String updateClass(@PathVariable("classId") String classId) {
        return "class.html";
    }

}
