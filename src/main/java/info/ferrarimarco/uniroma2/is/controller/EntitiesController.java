package info.ferrarimarco.uniroma2.is.controller;

import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/entities")
public class EntitiesController {
    
    @Autowired
    private ProductPersistenceService productService;

    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.GET)
    public String index(@PathVariable("entityName") String entityName, Model model, Pageable pageable) {
        if(StringUtils.isBlank(entityName)){
           throw new IllegalArgumentException("Entity name cannot be null"); 
        }
        
        String viewName = null;
        
        if("product".equals(entityName)){
            model.addAttribute("allEntitiesPage", productService.findAll(pageable));
            viewName = "products.html";
        }else{
            throw new IllegalArgumentException("Entity name not valid"); 
        }
        
        model.addAttribute("entityName", entityName);
        
        return viewName;
    }
    
    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.POST)
    public String createClass(@PathVariable("entityName") String entityName) {
        return "products.html";
    }
    
    @RequestMapping(value = {"{entityName}/{entityId}", "/{entityName}/{entityId}"}, method = RequestMethod.PUT)
    public String updateClass(
            @PathVariable("entityName") String entityName,
            @PathVariable("entityId") String entityId) {
        return "products.html";
    }
}
