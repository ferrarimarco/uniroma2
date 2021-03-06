package info.ferrarimarco.uniroma2.is.controller.spring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;

import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;

@WebAppConfiguration
public class EntitiesControllerTest extends AbstractControllerTest {
    
    @InjectMocks
    private EntitiesController entitiesController;
    
    @Test(groups = { "unitTests" }, expectedExceptions = NestedServletException.class)
    public void indexNonHandledEntityTest() throws Exception{
        mockMvc.perform(get("/entities/invalidName")).andExpect(status().isInternalServerError());
    }
    
    @Test(groups = { "unitTests" })
    public void indexProductTest() throws Exception{
        String entityName = Constants.PRODUCT_ENTITY_NAME;
        MvcResult result = mockMvc.perform(get("/entities/" + entityName))
                .andExpect(status().isOk())
                .andReturn();
        
        ModelAndView modelAndView = result.getModelAndView();
        assertThat(modelAndView.getViewName(), equalTo(Constants.PRODUCTS_VIEW_NAME));
        
        @SuppressWarnings("unchecked")
        Page<Product> allEntitiesPage = (Page<Product>) modelAndView.getModelMap().get(Constants.ALL_ENTITIES_PAGE_MODEL_KEY);
        assertThat(allEntitiesPage.getNumberOfElements(), equalTo(productsPage.getNumberOfElements()));
        assertThat(allEntitiesPage.getContent().get(0), equalTo(product));
        assertThat((String) modelAndView.getModelMap().get(Constants.ENTITY_NAME_MODEL_KEY), equalTo(entityName));
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = NestedServletException.class)
    public void getEntityNonHandledEntityTest() throws Exception{
        mockMvc.perform(get("/entities/invalidName/invalidId")).andExpect(status().isInternalServerError());
    }
    
    @Test(groups = { "unitTests" })
    public void getEntityTest() throws Exception{
        MvcResult result = mockMvc.perform(get("/entities/product/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn();
        Product resultProduct = JsonPath.parse(result.getResponse().getContentAsString()).read("$", Product.class);
        assertThat(resultProduct, equalTo(product));
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = NestedServletException.class)
    public void postEntityNotHandledTest() throws Exception{
        mockMvc.perform(post("/entities/invalidName")
                .param("save", "save")).andExpect(status().isInternalServerError());
    }
    
    @Test(groups = { "unitTests" })
    public void postEntityProductTest() throws Exception{
        mockMvc.perform(post("/entities/" + Constants.PRODUCT_ENTITY_NAME)
                    .param("clazzId", clazz.getId())
                    .param("brand", product.getBrand())
                    .param("name", product.getName())
                    .param("save", "save"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andReturn();
        verify(createUpdateProductApplicationController, times(1)).createNewProduct(eq(clazz.getId()), notNull(Product.class));
    }

    @Override
    protected AbstractController getController() {
        return entitiesController;
    }
}
