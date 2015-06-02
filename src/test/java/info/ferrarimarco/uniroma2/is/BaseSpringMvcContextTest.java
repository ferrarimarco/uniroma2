package info.ferrarimarco.uniroma2.is;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;

public class BaseSpringMvcContextTest extends BaseSpringMvcTest{
    @Autowired
    protected WebApplicationContext webApplicationContext;
    
    @BeforeClass(groups = {"springMvcTestInit"}, dependsOnGroups = {"springBaseTestInit"})
    protected void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
