package info.ferrarimarco.uniroma2.msa.resourcesharing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;

public abstract class BaseSpringTest extends AbstractTestNGSpringContextTests {

	@Autowired
	protected ApplicationContext applicationContext;
	
	@BeforeClass
	protected void setup() throws Exception {
		assertThat(applicationContext, notNullValue());
	}
}
