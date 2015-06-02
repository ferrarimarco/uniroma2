package info.ferrarimarco.uniroma2.is;

import lombok.extern.slf4j.Slf4j;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Slf4j
public abstract class BaseSpringMvcSingleControllerTest extends BaseSpringMvcTest{
    
    protected void setupMockMvc(Object... controllers){
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllers).build();
    }

    @Override
    protected void setupMockMvc() {
        log.warn("This class does context initialization. Initialize single controllers instead.");
        throw new UnsupportedOperationException("This class does context initialization. Initialize single controllers instead.");
    }
}
