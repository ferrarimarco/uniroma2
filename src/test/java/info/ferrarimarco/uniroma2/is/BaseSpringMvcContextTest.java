package info.ferrarimarco.uniroma2.is;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@Slf4j
public abstract class BaseSpringMvcContextTest extends BaseSpringMvcTest{
    @Autowired
    protected WebApplicationContext webApplicationContext;
    
    @Override
    protected void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();
        super.setupBaseSpringMvcTest();
    }

    @Override
    protected void setupMockMvc(Object[] controllers) {
        log.warn("This class does not support standalone setup for controllers. It will initialize the whole context instead");
        setupMockMvc();
    }
    
    @Override
    protected void setupMockMvc(Object[] controllers, HandlerMethodArgumentResolver[] argumentResolvers){
        setupMockMvc(null);
    }
}
