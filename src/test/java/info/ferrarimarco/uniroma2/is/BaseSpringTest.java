package info.ferrarimarco.uniroma2.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;

public abstract class BaseSpringTest extends AbstractTestNGSpringContextTests {
    @BeforeClass(groups = {"springBaseTestInit"})
    protected void setupBaseTest() throws Exception {
        assertThat(applicationContext, notNullValue());
    }
}