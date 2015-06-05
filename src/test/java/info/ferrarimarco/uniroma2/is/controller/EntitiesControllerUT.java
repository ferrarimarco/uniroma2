package info.ferrarimarco.uniroma2.is.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;

import java.util.ArrayList;
import java.util.List;

import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;
import org.testng.annotations.Test;


@WebAppConfiguration
public class EntitiesControllerUT extends AbstractControllerUT {
    
    @InjectMocks
    private EntitiesController entitiesController;
    
    @Test(expectedExceptions = NestedServletException.class)
    public void indexNonHandledEntityTest() throws Exception{
        mockMvc.perform(get("/entities/invalidName")).andExpect(status().isInternalServerError());
    }
    
    @Test(groups = { "unitTests", "springServicesTestGroup", "genericModelAttributesNeeded" })
    public void indexProductTest() throws Exception{
        Product product = Product.builder().category(category).clazz(clazz).brand("product-brand").build();
        product.setId("product-id");

        List<Product> products = new ArrayList<>();
        products.add(product);
        Page<Product> productsPage = new PageImpl<Product>(products);
        
        when(productPersistenceService.findAll(notNull(PageRequest.class))).thenReturn(productsPage);
        String entityName = "product";
        MvcResult result = mockMvc.perform(get("/entities/" + entityName)).andExpect(status().isOk()).andReturn();
        
        verify(productPersistenceService, times(1)).findAll(notNull(PageRequest.class));
        verify(productInstancePersistenceService, times(1)).countInstancesByProductId(product.getId());
        
        ModelAndView modelAndView = result.getModelAndView();
        assertThat(modelAndView.getViewName(), equalTo(EntitiesController.PRODUCTS_VIEW_NAME));
        
        @SuppressWarnings("unchecked")
        Page<Product> allEntitiesPage = (Page<Product>) modelAndView.getModelMap().get(EntitiesController.ALL_ENTITIES_PAGE_MODEL_KEY);
        assertThat(allEntitiesPage.getNumberOfElements(), equalTo(productsPage.getNumberOfElements()));
        assertThat(allEntitiesPage.getContent().get(0), equalTo(product));
        assertThat((String) modelAndView.getModelMap().get(Constants.ENTITY_NAME_MODEL_KEY), equalTo(entityName));
    }

    @Override
    protected AbstractController getController() {
        return entitiesController;
    }
}
