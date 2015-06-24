package info.ferrarimarco.uniroma2.is.controller.spring;

import lombok.extern.slf4j.Slf4j;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.ProductInstance;
import info.ferrarimarco.uniroma2.is.model.dto.InstanceDto;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto.Operation;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@Slf4j
public class EntitiesController extends AbstractController{
    
    public static final String PRODUCTS_VIEW_NAME = "products.html";
    public static final String ALL_ENTITIES_PAGE_MODEL_KEY = "allEntitiesPage";

    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.GET)
    public String index(@PathVariable("entityName") String entityName, Model model, Pageable pageable) {
        String viewName = null;

        if("product".equals(entityName) || "productInstance".equals(entityName)){
            Page<Product> allEntitesPage = productPersistenceService.findAll(pageable);
            if(allEntitesPage != null)
                for(Product product : allEntitesPage){
                    product.setAmount(productInstancePersistenceService.countInstancesByProductId(product.getId()));
                }
            model.addAttribute(ALL_ENTITIES_PAGE_MODEL_KEY, allEntitesPage);
            model.addAttribute(new ProductDto());
            model.addAttribute(new InstanceDto());
            viewName = EntitiesController.PRODUCTS_VIEW_NAME;
        }else{
            throw new IllegalArgumentException("Entity name not valid: " + entityName);
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
        }else{
            throw new IllegalArgumentException("Entity name not valid");
        }
    }

    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.POST)
    public String postEntity(@PathVariable("entityName") String entityName, Model model, Pageable pageable, @ModelAttribute ProductDto productDto, @ModelAttribute InstanceDto instanceDto) {
        if ("product".equals(entityName)) {
            if (productDto.getClazz() == null && !StringUtils.isBlank(productDto.getClazzId()))
                productDto.setClazz(clazzPersistenceService.findById(productDto.getClazzId()));
            else
                throw new IllegalArgumentException("Product class cannot be null");
            
            if(productDto.getCategory() == null){
                productDto.setCategory(productDto.getClazz().getCategory());
            }
            
            // This may be an update request
            if (productDto.getId() != null && productPersistenceService.exists(productDto.getId())) { // Update
                productDto.setId(productDto.getId());
            } else {
                productDto.setId(null);
            }
            
            Product p = productPersistenceService.save(productDto.asProductClone());
            log.info("Saved product: {}", p);
            return index(entityName, model, pageable);
        }else if("productInstance".equals(entityName)){
            Product product = productPersistenceService.findById(instanceDto.getProductId());
            if(product == null){
                throw new NullPointerException("product");
            }
            
            if(Operation.ADD_INSTANCES.equals(instanceDto.getOperation())){
                productInstancePersistenceService.save(instanceDto.asProductInstanceClone());
                product.setStocked(product.getStocked() + instanceDto.getNewAmount());
                log.info("Added {} instances for {}", instanceDto.getNewAmount(), product);
            }else if(Operation.REMOVE_INSTANCES.equals(instanceDto.getOperation())){
                log.info("Removing {} instances for {}", instanceDto.getNewAmount(), product);
                Long count = productInstancePersistenceService.countInstancesByProductId(instanceDto.getProductId());
                if(count > instanceDto.getNewAmount()){
                    product.setRequested(product.getRequested() + instanceDto.getNewAmount());
                    product.setDispensed(product.getDispensed() + instanceDto.getNewAmount());
                    
                    Page<ProductInstance> productInstances = null;
                    int pageIndex = -1;
                    do{
                        productInstances = productInstancePersistenceService.findByProductId(product.getId(), new PageRequest(++pageIndex, 10));
                        
                        for(int i = 0; i < productInstances.getContent().size() && instanceDto.getNewAmount() > 0; i++){
                            ProductInstance productInstance = productInstances.getContent().get(i);
                            if(productInstance.getAmount() <= instanceDto.getNewAmount()){
                                instanceDto.setNewAmount(instanceDto.getNewAmount() - productInstance.getAmount());
                                productInstancePersistenceService.delete(productInstance.getId());
                            }else{
                                productInstance.setAmount(productInstance.getAmount() - instanceDto.getNewAmount());
                                productInstancePersistenceService.save(productInstance);
                                instanceDto.setNewAmount(0L);
                            }
                        }
                    }
                    while(instanceDto.getNewAmount() > 0);
                }else{
                    // TODO: set error (required too many instances)
                    product.setRequested(product.getRequested() + (instanceDto.getNewAmount() - count));
                }
            }
            
            productPersistenceService.save(product);
        }else{
            throw new IllegalArgumentException("Entity name not valid");
        }
        
        return index(entityName, model, pageable);
    }
    
    @RequestMapping(value = {"{entityName}/delete/expired", "/{entityName}/delete/expired"}, method = RequestMethod.GET)
    public String removeExpiredEntities(@PathVariable("entityName") String entityName, Model model){
        if(StringUtils.isBlank(entityName)){
            throw new IllegalArgumentException("Entity name cannot be null");
        }

        if("productInstance".equals(entityName)){
            productInstancePersistenceService.deleteExpired();
        }else{
            throw new IllegalArgumentException("Entity name not valid");
        }
        
        log.info("Removed expired instances for {}", entityName);
        
        return index(entityName, model, null);
    }
}
