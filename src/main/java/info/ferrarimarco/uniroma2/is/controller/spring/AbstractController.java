package info.ferrarimarco.uniroma2.is.controller.spring;

import info.ferrarimarco.uniroma2.is.controller.application.LoadEntityApplicationController;
import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public class AbstractController {
    @Autowired
    protected LoadEntityApplicationController loadEntityApplicationController;
    
    @ModelAttribute("allClasses")
    protected List<Clazz> getAllClasses(){
        return loadEntityApplicationController.getAllClasses();
    }
    
    @ModelAttribute("allCategories")
    protected List<Category> getAllCategories(){
        return loadEntityApplicationController.getAllCategories();
    }
}
