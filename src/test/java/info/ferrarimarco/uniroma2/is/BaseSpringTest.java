package info.ferrarimarco.uniroma2.is;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import info.ferrarimarco.uniroma2.is.config.context.RootConfig;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { RootConfig.class })
public abstract class BaseSpringTest extends AbstractTestNGSpringContextTests {

    @BeforeClass
    @Test(groups = {"checkintest" })
    protected void setup() throws Exception {
        assertThat(applicationContext, notNullValue());
    }
    
}