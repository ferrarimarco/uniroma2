package info.ferrarimarco.uniroma2.is.controller;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.dto.EntityIdListDto;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/entities")
public class EntitiesController {

    @Autowired
    private ProductPersistenceService productPersistenceService;

    @Autowired
    private ClazzPersistenceService clazzPersistenceService;

    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.GET)
    public String index(@PathVariable("entityName") String entityName, Model model, Pageable pageable) {
        if(StringUtils.isBlank(entityName)){
            throw new IllegalArgumentException("Entity name cannot be null"); 
        }

        String viewName = null;

        if("product".equals(entityName)){
            model.addAttribute("allEntitiesPage", productPersistenceService.findAll(pageable));
            model.addAttribute("allClasses", clazzPersistenceService.findAll());
            viewName = "products.html";
        }else{
            throw new IllegalArgumentException("Entity name not valid"); 
        }
        
        // TODO: move "entityName" to constants
        model.addAttribute("entityName", entityName);
        model.addAttribute(Constants.PRODUCT_DTO_MODEL_KEY, new ProductDto());
        model.addAttribute(Constants.ENTITY_ID_LIST_MODEL_KEY, new EntityIdListDto());

        return viewName;
    }

    @RequestMapping(value = {"{entityName}/{entityId}", "/{entityName}/{entityId}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Product getEntity(@PathVariable("entityName") String entityName,
            @PathVariable("entityId") String entityId){
        if("product".equals(entityName)){
            return productPersistenceService.findById(entityId);
        }

        return null;
    }

    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.POST)
    public String createEntity(@PathVariable("entityName") String entityName, Model model, Pageable pageable, @ModelAttribute ProductDto productDto) {
        if(productDto.getClazz() == null){
            productDto.setClazz(clazzPersistenceService.findById(productDto.getClazzId())); 
        }
        
        // This may be an update request
        if(productDto.getId() != null && productPersistenceService.exists(productDto.getId())){
            productDto.setId(productDto.getId());
            productDto.setSymbolicId(productPersistenceService.findById(productDto.getId()).getSymbolicId());
        }else{
            productDto.setId(null);
        }

        productPersistenceService.save(productDto.asProductClone());
        return index(entityName, model, pageable);
    }
}
