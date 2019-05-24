package info.ferrarimarco.uniroma2.is.controller.spring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.Test;

import info.ferrarimarco.uniroma2.is.AbstractIT;
import info.ferrarimarco.uniroma2.is.config.context.RootConfig;
import info.ferrarimarco.uniroma2.is.config.servlet.SpringMvcConfig;

@ContextConfiguration(classes = {RootConfig.class, SpringMvcConfig.class})
@WebAppConfiguration
public class WebJarsControllerIT extends AbstractIT {
    
    @InjectMocks
    private WebJarsController webJarsController;
    
    @Test(groups = { "integrationTests" })
    public void webjarsJsTest() throws Exception{
        mockMvc.perform(get("/webjars/webjarslocator/jquery.min.js"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/javascript"))
                .andReturn();
    }
    
    @Test(groups = { "integrationTests" })
    public void webjarsCssTest() throws Exception{
        mockMvc.perform(get("/webjars/webjarslocator/bootstrap.min.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/css"))
                .andReturn();
    }
}
