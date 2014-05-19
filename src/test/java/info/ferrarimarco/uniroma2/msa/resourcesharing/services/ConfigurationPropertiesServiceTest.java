package info.ferrarimarco.uniroma2.msa.resourcesharing.services;

import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ContextConfiguration("classpath:spring-context.xml")
public class ConfigurationPropertiesServiceTest extends BaseSpringTest{
	
	//private static Logger logger = LoggerFactory.getLogger(ConfigurationPropertiesServiceTest.class);
	
	@Autowired
	private ConfigurationPropertiesService configurationPropertiesService;
	
    @BeforeClass
    protected void setup() throws Exception {
    	super.setup();
    	assertThat(configurationPropertiesService, notNullValue());
    }
    
    @Test(groups = {"ConfigurationPropertiesServiceTestGroup","springServicesTestGroup"})
    public void propertiesLoadTest() throws IOException {
    	Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationPropertiesService.getPropetiesFilePath()));
    	
		String cryptographyProvider = properties.getProperty("security.provider");
		assertThat(cryptographyProvider, equalTo(configurationPropertiesService.getCryptographyProvider()));
		
		String secureHashAlgorithm = properties.getProperty("security.hashing.algorithm");
		assertThat(secureHashAlgorithm, equalTo(configurationPropertiesService.getHashingAlgorithm()));
    }
}
