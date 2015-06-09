package info.ferrarimarco.uniroma2.is;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@Slf4j
public abstract class BaseSpringMvcSingleControllerTest extends BaseSpringMvcTest{

    @Override
    protected void setupMockMvc() {
        log.warn("This class does context initialization. Initialize single controllers instead.");
        throw new UnsupportedOperationException("This class does context initialization. Initialize single controllers instead.");
    }
    
    @Override
    protected void setupMockMvc(Object[] controllers){
        setupMockMvc(controllers, null);
    }
    
    @Override
    protected void setupMockMvc(Object[] controllers, HandlerMethodArgumentResolver[] argumentResolvers){
        StandaloneMockMvcBuilder builder = MockMvcBuilders.standaloneSetup(controllers);
        if(ArrayUtils.isEmpty(argumentResolvers))
            this.mockMvc = builder.build();
        else
            this.mockMvc = builder.setCustomArgumentResolvers(argumentResolvers).build();
        
        super.setupBaseSpringMvcTest();
    }
}
