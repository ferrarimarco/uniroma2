package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

public class GcmMessageHandlerTest extends BaseSpringTest{
    
    @InjectMocks
    private GcmMessageHandler gcmMessageHandler;
    
    @Mock
    private GcmMessageSender gcmMessageSender;

    @Mock
    private UserPersistenceService userPersistenceService;

    @Mock
    private ResourcePersistenceService resourcePersistenceService;

    @BeforeClass
    protected void setup() throws Exception {
        super.setup();
        MockitoAnnotations.initMocks(this);
        assertThat(gcmMessageHandler, notNullValue());
        
        when(userPersistenceService.storeUser()).doNothing();
    }
    
    @AfterTest
    protected void setupTest() {
        // Reset mocks after each test to avoid leaks
        Mockito.reset(gcmMessageSender);
        Mockito.reset(userPersistenceService);
        Mockito.reset(resourcePersistenceService);
    }
    
    @Test(enabled = false)
    public void handleIncomingUpdateUserDetailsMessageTest() {
        
    }
}
