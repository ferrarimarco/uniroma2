package info.ferrarimarco.uniroma2.is.controller.spring;

import info.ferrarimarco.uniroma2.is.controller.application.AddRemoveProductInstanceApplicationController;
import info.ferrarimarco.uniroma2.is.controller.application.CreateUpdateProductApplicationController;
import info.ferrarimarco.uniroma2.is.controller.application.DeleteProductApplicationController;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.dto.InstanceDto;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto.Operation;

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
public class EntitiesController extends AbstractController{
    
    @Autowired
    private CreateUpdateProductApplicationController createUpdateProductApplicationController;
    
    @Autowired
    private AddRemoveProductInstanceApplicationController addRemoveProductInstanceController;

    @Autowired
    private DeleteProductApplicationController deleteProductApplicationController;

    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.GET)
    public String index(@PathVariable("entityName") String entityName, Model model, Pageable pageable) {
        String viewName = null;

        if(Constants.PRODUCT_ENTITY_NAME.equals(entityName)){
            Page<Product> allEntitesPage = loadEntityApplicationController.loadProductPage(pageable);
            model.addAttribute(Constants.ALL_ENTITIES_PAGE_MODEL_KEY, allEntitesPage);
            model.addAttribute(new ProductDto());
            model.addAttribute(new InstanceDto());
            viewName = Constants.PRODUCTS_VIEW_NAME;
        }else
            throw new IllegalArgumentException("Entity name not valid: " + entityName);
        
        model.addAttribute(Constants.ENTITY_NAME_MODEL_KEY, entityName);
        return viewName;
    }

    @RequestMapping(value = {"{entityName}/{entityId}", "/{entityName}/{entityId}"}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Product getEntity(@PathVariable("entityName") String entityName,
            @PathVariable("entityId") String entityId){
        if(Constants.PRODUCT_ENTITY_NAME.equals(entityName))
            return loadEntityApplicationController.loadProduct(entityId);
        else
            throw new IllegalArgumentException("Entity name not valid");
    }

    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.POST, params = "save")
    public String postEntity(@PathVariable("entityName") String entityName, @ModelAttribute ProductDto productDto, @ModelAttribute InstanceDto instanceDto) {
        if (Constants.PRODUCT_ENTITY_NAME.equals(entityName)) {
            // This may be an update request: it's an update if productDto has an ID
            if (StringUtils.isBlank(productDto.getId()))
                createUpdateProductApplicationController.createNewProduct(productDto.getClazzId(), productDto.asProductClone());
            else
                createUpdateProductApplicationController.updateExistingProduct(productDto.getClazzId(), productDto.asProductClone());
        }else if(Constants.PRODUCT_INSTANCE_ENTITY_NAME.equals(entityName)){
            if(Operation.ADD_INSTANCES.equals(instanceDto.getOperation()))
                addRemoveProductInstanceController.addProductInstance(instanceDto.asProductInstanceClone(), instanceDto.getNewAmount());
            else if(Operation.REMOVE_INSTANCES.equals(instanceDto.getOperation()))
                addRemoveProductInstanceController.removeProductInstance(instanceDto.asProductInstanceClone(), instanceDto.getNewAmount());
        }else
            throw new IllegalArgumentException("Entity name not valid");
        
        return "redirect:/entities/product";
    }
    
    @RequestMapping(value = {"{entityName}", "/{entityName}"}, method = RequestMethod.POST, params = "delete")
    public String deleteEntity(@PathVariable("entityName") String entityName, @ModelAttribute ProductDto productDto, @ModelAttribute InstanceDto instanceDto) {
        if (Constants.PRODUCT_ENTITY_NAME.equals(entityName)) {
            if (StringUtils.isBlank(productDto.getId()))
                throw new IllegalArgumentException("Entity ID cannot be null");
            else
                deleteProductApplicationController.deleteExistingProduct(productDto.getId());
        }else
            throw new IllegalArgumentException("Entity name not valid");
        
        return "redirect:/entities/product";
    }
    
    @RequestMapping(value = {"{entityName}/delete/expired", "/{entityName}/delete/expired"}, method = RequestMethod.GET)
    public String removeExpiredEntities(@PathVariable("entityName") String entityName){
        if(Constants.PRODUCT_INSTANCE_ENTITY_NAME.equals(entityName))
            addRemoveProductInstanceController.removeExpiredProductInstances();
        else
            throw new IllegalArgumentException("Entity name not valid");
        
        return "redirect:/entities/product";
    }
}
