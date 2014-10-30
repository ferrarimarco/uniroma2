package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message.GcmMessageHandler.GcmMessageAction;
import info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message.GcmMessageHandler.GcmMessageField;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;

import java.util.HashMap;
import java.util.Map;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GcmMessageHandlerTest {

    @Mock
    private GcmMessageSender gcmMessageSender;

    @Mock
    private UserPersistenceService userPersistenceService;

    @Mock
    private ResourcePersistenceService resourcePersistenceService;

    @InjectMocks
    private GcmMessageHandler gcmMessageHandler;

    @BeforeClass
    protected void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        assertThat(gcmMessageHandler, notNullValue());
    }

    @Test
    public void handleIncomingUpdateUserDetailsMessageTest() {
        when(userPersistenceService.storeUser(notNull(ResourceSharingUser.class))).thenReturn(null);

        Map<String, Object> jsonObject = new HashMap<>();

        Map<String, String> payload = new HashMap<>();

        jsonObject.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-sender-gcm-id");
        jsonObject.put(GcmMessageField.MESSAGE_ID.getStringValue(), "test-message-id");
        jsonObject.put(GcmMessageField.MESSAGE_CATEGORY.getStringValue(), "test-message-category");

        payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.UPDATE_USER_DETAILS.getStringValue());
        payload.put(GcmMessageField.DATA_CREATOR_ID.getStringValue(), "test-creator-id");
        payload.put(GcmMessageField.DATA_ADDRESS.getStringValue(), "test-address");
        payload.put(GcmMessageField.DATA_LOCALITY.getStringValue(), "test-locality");
        payload.put(GcmMessageField.DATA_COUNTRY.getStringValue(), "test-country");
        payload.put(GcmMessageField.DATA_LATITUDE.getStringValue(), "0.0");
        payload.put(GcmMessageField.DATA_LONGITUDE.getStringValue(), "0.0");
        payload.put(GcmMessageField.DATA_MAX_DISTANCE.getStringValue(), "100");
        payload.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-from");

        jsonObject.put(GcmMessageField.MESSAGE_DATA.getStringValue(), payload);

        gcmMessageHandler.handleIncomingDataMessage(jsonObject);

        verify(gcmMessageSender).sendJsonAck((String) jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()), (String) jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()));
        verify(userPersistenceService).storeUser(notNull(ResourceSharingUser.class));

        // Reset mocks after each test to avoid leaks
        Mockito.reset(gcmMessageSender);
        Mockito.reset(userPersistenceService);
    }

    @Test
    public void handleIncomingDeleteResourceMessageTest() {
        Map<String, Object> jsonObject = new HashMap<>();

        Map<String, String> payload = new HashMap<>();

        jsonObject.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-sender-gcm-id");
        jsonObject.put(GcmMessageField.MESSAGE_ID.getStringValue(), "test-message-id");
        jsonObject.put(GcmMessageField.MESSAGE_CATEGORY.getStringValue(), "test-message-category");

        payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.DELETE_MY_RESOURCE.getStringValue());
        payload.put(GcmMessageField.DATA_CREATOR_ID.getStringValue(), "test-creator-id");
        payload.put(GcmMessageField.DATA_CREATION_TIME.getStringValue(), "1000");

        jsonObject.put(GcmMessageField.MESSAGE_DATA.getStringValue(), payload);

        when(resourcePersistenceService.readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()))).thenReturn(
                new ResourceSharingResource("test-resource-id"));

        gcmMessageHandler.handleIncomingDataMessage(jsonObject);

        verify(gcmMessageSender).sendJsonAck((String) jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()), (String) jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()));
        verify(resourcePersistenceService).readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()));
        verify(resourcePersistenceService).deleteResource("test-resource-id");

        // Reset mocks after each test to avoid leaks
        Mockito.reset(gcmMessageSender);
        Mockito.reset(userPersistenceService);
        Mockito.reset(resourcePersistenceService);
    }
}
