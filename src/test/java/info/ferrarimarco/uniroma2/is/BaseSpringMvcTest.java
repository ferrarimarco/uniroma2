package info.ferrarimarco.uniroma2.is;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.testng.annotations.BeforeClass;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public abstract class BaseSpringMvcTest extends BaseSpringTest{
    protected MockMvc mockMvc;
    
    protected abstract void setupMockMvc();
    protected abstract void setupMockMvc(Object[] controllers);
    protected abstract void setupMockMvc(Object[] controllers, HandlerMethodArgumentResolver[] argumentResolvers);
    
    @BeforeClass(groups = {"springMvcTestInit"}, dependsOnGroups = {"springUTinit"})
    protected void mockMvcInitTest(){
        assertThat(mockMvc, notNullValue());
        
        // Configure json-path
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }
}