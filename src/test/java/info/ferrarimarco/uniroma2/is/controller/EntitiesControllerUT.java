package info.ferrarimarco.uniroma2.is.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.InjectMocks;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
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
    
    @Test
    public void indexProductTest() throws Exception{
        ResultActions result = mockMvc.perform(get("/entities/product")).andExpect(status().isOk());
    }

    @Override
    protected AbstractController getController() {
        return entitiesController;
    }
}
