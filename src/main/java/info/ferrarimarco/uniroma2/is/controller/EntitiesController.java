package info.ferrarimarco.uniroma2.is.controller;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.dto.InstanceDto;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto.Operation;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private ProductInstancePersistenceService productInstancePersistenceService;

    @Autowired
    private ClazzPersistenceService clazzPersistenceService;
    
    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.GET)
    public String index(@PathVariable("entityName") String entityName, Model model, Pageable pageable) {
        if(StringUtils.isBlank(entityName)){
            throw new IllegalArgumentException("Entity name cannot be null");
        }

        String viewName = null;

        if("product".equals(entityName) || "instance".equals(entityName)){
            Page<Product> allEntitesPage = productPersistenceService.findAll(pageable);
            for(Product product : allEntitesPage){
                product.setAmount(productInstancePersistenceService.countInstancesByProductId(product.getId()));
            }
            model.addAttribute("allEntitiesPage", allEntitesPage);
            model.addAttribute("allClasses", clazzPersistenceService.findAll());
            model.addAttribute(Constants.PRODUCT_DTO_MODEL_KEY, new ProductDto());
            model.addAttribute(Constants.INSTANCE_DTO_MODEL_KEY, new InstanceDto());
            viewName = "products.html";
        }else{
            throw new IllegalArgumentException("Entity name not valid");
        }
        
        model.addAttribute(Constants.ENTITY_NAME_MODEL_KEY, entityName);
        
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
    public String createEntity(@PathVariable("entityName") String entityName, Model model, Pageable pageable, @ModelAttribute ProductDto productDto, @ModelAttribute InstanceDto instanceDto) {
        if(StringUtils.isBlank(entityName)){
            throw new IllegalArgumentException("Entity name cannot be null");
        }
        
        if ("product".equals(entityName)) {
            if (productDto.getClazz() == null) {
                productDto.setClazz(clazzPersistenceService.findById(productDto.getClazzId()));
            }
            // This may be an update request
            if (productDto.getId() != null && productPersistenceService.exists(productDto.getId())) { // Update
                productDto.setId(productDto.getId());
            } else {
                productDto.setId(null);
            }
            
            productPersistenceService.save(productDto.asProductClone());
            return index(entityName, model, pageable);
        }if("productInstance".equals(entityName)){
            Product product = productPersistenceService.findById(instanceDto.getProductId());
            if(product == null){
                throw new NullPointerException("product");
            }
            
            if(Operation.ADD_INSTANCES.equals(instanceDto.getOperation())){
                productInstancePersistenceService.save(instanceDto.asProductInstanceClone());
            }else if(Operation.REMOVE_INSTANCES.equals(instanceDto.getOperation())){
                Long count = productInstancePersistenceService.countInstancesByProductId(instanceDto.getProductId());
                if(count < instanceDto.getNewAmount()){
                    instanceDto.setNewAmount(count - instanceDto.getNewAmount());
                    product.setRequested(product.getRequested() + count);
                    product.setDispensed(product.getDispensed() + count);
                }else{
                    // TODO: set error (required too many instances)
                    // TODO: update indexes
                }
            }
            
            return index("product", model, pageable);
        }else{
            throw new IllegalArgumentException("Entity name not valid");
        }
    }
}
