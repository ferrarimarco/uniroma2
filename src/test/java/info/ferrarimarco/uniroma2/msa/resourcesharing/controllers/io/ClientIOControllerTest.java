package info.ferrarimarco.uniroma2.msa.resourcesharing.controllers.io;

import java.nio.charset.Charset;

import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.PersistenceLayerInitializationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration({"classpath:spring-context.xml","classpath:spring-io-mvc-dispatcher-servlet.xml"})
@WebAppConfiguration
public class ClientIOControllerTest extends AbstractTestNGSpringContextTests {

	private static Logger logger = LoggerFactory.getLogger(ClientIOControllerTest.class);
	
	private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private PersistenceLayerInitializationService persistenceLayerInitializationService;
	
	private int usersToInitialize;

	@BeforeClass
	protected void setup() throws Exception {
		Assert.assertNotNull(webApplicationContext);
		Assert.assertNotNull(persistenceLayerInitializationService);

		//We have to reset our mock between tests because the mock objects
		//are managed by the Spring container. If we would not reset them,
		//stubbing and verified behavior would "leak" from one test to another.
		//Mockito.reset(userPersistenceService);

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		usersToInitialize = 10;
	}

	@BeforeMethod(alwaysRun = true)
	protected void initMethod() {
		persistenceLayerInitializationService.initializeUserRepository(usersToInitialize);
	}

	@Test
	public void getUserByEmailTest() throws Exception {
		
		String email = PersistenceLayerInitializationService.USER_NAME_PREFIX + "0" + PersistenceLayerInitializationService.EMAIL_SUFFIX;
		
		MvcResult mvcResult = mockMvc.perform(get("/client/user/" + email))
		.andExpect(status().isOk())
		.andExpect(content().contentType(APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(1))).andReturn();
		
		String resultContent = mvcResult.getResponse().getContentAsString();
		
		logger.info("User: {}", resultContent);
	}

	@AfterMethod(alwaysRun = true)
	protected void cleanupMethod() {
		persistenceLayerInitializationService.cleanupUserRepository();
	}
}
