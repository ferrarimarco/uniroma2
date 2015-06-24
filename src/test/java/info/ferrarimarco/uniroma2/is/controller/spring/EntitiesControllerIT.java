package info.ferrarimarco.uniroma2.is.controller.spring;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import info.ferrarimarco.uniroma2.is.AbstractIT;
import info.ferrarimarco.uniroma2.is.config.context.RootConfig;
import info.ferrarimarco.uniroma2.is.config.servlet.SpringMvcConfig;
import info.ferrarimarco.uniroma2.is.controller.spring.EntitiesController;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;
import org.testng.annotations.Test;

@ContextConfiguration(classes = {RootConfig.class, SpringMvcConfig.class})
@WebAppConfiguration
public class EntitiesControllerIT extends AbstractIT{

    @Autowired
    private EntitiesController entitiesController;

    @Test(groups = { "integrationTests" }, expectedExceptions = NestedServletException.class)
    public void indexNonHandledEntityTest() throws Exception{
        mockMvc.perform(get("/entities/invalidName")
                .with(csrf())
                .with(user("admin").password("password").roles("USER","ADMIN")))
                .andExpect(status().isInternalServerError());
    }

    @Test(groups = { "integrationTests" })
    public void indexProductTest() throws Exception{
        String entityName = "product";
        MvcResult result = mockMvc.perform(get("/entities/" + entityName)
                .with(csrf())
                .with(user("admin").password("password").roles("USER","ADMIN")))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        assertThat(modelAndView.getViewName(), equalTo(EntitiesController.PRODUCTS_VIEW_NAME));

        @SuppressWarnings("unchecked")
        Page<Product> allEntitiesPage = (Page<Product>) modelAndView.getModelMap().get(EntitiesController.ALL_ENTITIES_PAGE_MODEL_KEY);
        assertThat(allEntitiesPage.getNumberOfElements(), is(greaterThanOrEqualTo(0)));
        assertThat((String) modelAndView.getModelMap().get(Constants.ENTITY_NAME_MODEL_KEY), equalTo(entityName));
    }

    @Test(groups = { "integrationTests" }, expectedExceptions = NestedServletException.class)
    public void postEntityNotHandledTest() throws Exception{
        mockMvc.perform(post("/entities/invalidName")
                .with(csrf())
                .with(user("admin").password("password").roles("USER","ADMIN")))
                .andExpect(status().isInternalServerError());
    }
}
