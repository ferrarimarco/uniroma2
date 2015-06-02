package info.ferrarimarco.uniroma2.is;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;

public class BaseSpringMvcSingleControllerTest extends BaseSpringMvcTest{
    protected MockMvc mockMvc;
    
    private Object controller;
    
    protected void setupMockMvc(Object controller){
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        this.controller = controller;
    }
    
    @BeforeClass(groups = {"springMvcTestInit"}, dependsOnGroups = {"springBaseTestInit"})
    protected void setup(){
        assertThat(controller, notNullValue());
    }
}
