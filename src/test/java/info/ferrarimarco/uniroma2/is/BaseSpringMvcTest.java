package info.ferrarimarco.uniroma2.is;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.testng.annotations.BeforeClass;

public abstract class BaseSpringMvcTest extends BaseSpringTest{
    protected MockMvc mockMvc;
    
    protected abstract void setupMockMvc();
    protected abstract void setupMockMvc(Object[] controllers);
    protected abstract void setupMockMvc(Object[] controllers, HandlerMethodArgumentResolver[] argumentResolvers);
    
    @BeforeClass(groups = {"springMvcTestInit"}, dependsOnGroups = {"springUTinit"})
    protected void mockMvcInitTest(){
        assertThat(mockMvc, notNullValue());
    }
}