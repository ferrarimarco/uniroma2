package info.ferrarimarco.uniroma2.msa.resourcesharing;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;

@ContextConfiguration("classpath:spring-context.xml")
public abstract class BaseSpringTest extends AbstractTestNGSpringContextTests {

	@BeforeClass
	protected void setup() throws Exception {
		assertThat(applicationContext, notNullValue());
	}
}
