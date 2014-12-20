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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
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

	@BeforeMethod
	protected void setupMethod() throws Exception {
		MockitoAnnotations.initMocks(this);
		assertThat(gcmMessageHandler, notNullValue());
	}

	@Test
	public void handleIncomingUpdateUserDetailsMessageTest() {
		Map<String, Object> jsonObject = new HashMap<>();
		jsonObject.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-sender-gcm-id");
		jsonObject.put(GcmMessageField.MESSAGE_ID.getStringValue(), "test-message-id");
		jsonObject.put(GcmMessageField.MESSAGE_CATEGORY.getStringValue(), "test-message-category");

		Map<String, String> payload = new HashMap<>();
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

		when(userPersistenceService.storeUser(notNull(ResourceSharingUser.class))).thenReturn(null);

		gcmMessageHandler.handleIncomingDataMessage(jsonObject);

		verify(gcmMessageSender).sendJsonAck((String) jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()), (String) jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()));
		verify(userPersistenceService).storeUser(notNull(ResourceSharingUser.class));
	}

	@Test
	public void handleIncomingDeleteResourceMessageNoBookerTest() {
		Map<String, Object> jsonObject = new HashMap<>();
		jsonObject.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-sender-gcm-id");
		jsonObject.put(GcmMessageField.MESSAGE_ID.getStringValue(), "test-message-id");
		jsonObject.put(GcmMessageField.MESSAGE_CATEGORY.getStringValue(), "test-message-category");

		Map<String, String> payload = new HashMap<>();
		payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.DELETE_MY_RESOURCE.getStringValue());
		payload.put(GcmMessageField.DATA_CREATOR_ID.getStringValue(), "test-creator-id");
		payload.put(GcmMessageField.DATA_CREATION_TIME.getStringValue(), "1000");

		jsonObject.put(GcmMessageField.MESSAGE_DATA.getStringValue(), payload);

		when(resourcePersistenceService.readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()))).thenReturn(
				new ResourceSharingResource("test-resource-id"));

		gcmMessageHandler.handleIncomingDataMessage(jsonObject);

		verify(gcmMessageSender).sendJsonAck((String) jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()), (String) jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()));
		verify(resourcePersistenceService).readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()));
	}

	@Test
	public void handleIncomingDeleteResourceMessageWithBookerTest() {
		Map<String, Object> jsonObject = new HashMap<>();
		jsonObject.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-sender-gcm-id");
		jsonObject.put(GcmMessageField.MESSAGE_ID.getStringValue(), "test-message-id");
		jsonObject.put(GcmMessageField.MESSAGE_CATEGORY.getStringValue(), "test-message-category");

		Map<String, String> payload = new HashMap<>();
		payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.DELETE_MY_RESOURCE.getStringValue());
		payload.put(GcmMessageField.DATA_CREATOR_ID.getStringValue(), "test-creator-id");
		payload.put(GcmMessageField.DATA_CREATION_TIME.getStringValue(), "1000");

		jsonObject.put(GcmMessageField.MESSAGE_DATA.getStringValue(), payload);

		ResourceSharingUser booker = new ResourceSharingUser(
				payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()),
				"test-creator-gcm-id",
				new DateTime(),
				"test-creator-address",
				"test-creator-locality",
				"test-creator-country",
				0.0,
				0.0,
				0);
		ResourceSharingResource resourceToDelete = new ResourceSharingResource("test-resource-id");
		resourceToDelete.setBookerId(booker.getUserId());

		when(resourcePersistenceService.readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()))).thenReturn(
				resourceToDelete);

		when(userPersistenceService.readUsersByUserId(booker.getUserId()))
		.thenReturn(booker);

		gcmMessageHandler.handleIncomingDataMessage(jsonObject);

		verify(gcmMessageSender).sendJsonAck((String) jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()), (String) jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()));
		verify(resourcePersistenceService).readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()));
		verify(resourcePersistenceService).deleteResource(resourceToDelete.getId());
		verify(userPersistenceService).readUsersByUserId(booker.getUserId());
	}

	@Test
	public void handleIncomingBookResourceMessageTest() {
		Map<String, Object> jsonObject = new HashMap<>();
		jsonObject.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-sender-gcm-id");
		jsonObject.put(GcmMessageField.MESSAGE_ID.getStringValue(), "test-message-id");
		jsonObject.put(GcmMessageField.MESSAGE_CATEGORY.getStringValue(), "test-message-category");

		Map<String, String> payload = new HashMap<>();
		payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.BOOK_RESOURCE.getStringValue());
		payload.put(GcmMessageField.DATA_CREATOR_ID.getStringValue(), "test-creator-id");
		payload.put(GcmMessageField.DATA_CREATION_TIME.getStringValue(), "1000");
		payload.put(GcmMessageField.DATA_TTL.getStringValue(), "1000");

		jsonObject.put(GcmMessageField.MESSAGE_DATA.getStringValue(), payload);

		// Configure mocks
		when(resourcePersistenceService.readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()))).thenReturn(
				new ResourceSharingResource("test-resource-id"));
		when(resourcePersistenceService.storeResource(notNull(ResourceSharingResource.class))).thenReturn(null);
		when(userPersistenceService.readUsersByUserId(payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue())))
		.thenReturn(new ResourceSharingUser(
				payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()),
				"test-creator-gcm-id",
				new DateTime(),
				"test-creator-address",
				"test-creator-locality",
				"test-creator-country",
				0.0,
				0.0,
				0));

		gcmMessageHandler.handleIncomingDataMessage(jsonObject);

		verify(gcmMessageSender).sendJsonAck((String) jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()), (String) jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()));
		verify(resourcePersistenceService).readResourceById(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()), payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()));
		verify(userPersistenceService).readUsersByUserId(payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()));
	}

	@Test(enabled = false)
	public void handleIncomingNewResourceMessageTest() {
		Map<String, Object> jsonObject = new HashMap<>();
		jsonObject.put(GcmMessageField.MESSAGE_FROM.getStringValue(), "test-sender-gcm-id");
		jsonObject.put(GcmMessageField.MESSAGE_ID.getStringValue(), "test-message-id");
		jsonObject.put(GcmMessageField.MESSAGE_CATEGORY.getStringValue(), "test-message-category");

		Map<String, String> payload = new HashMap<>();
		payload.put(GcmMessageField.MESSAGE_ACTION.getStringValue(), GcmMessageAction.NEW_RESOURCE_FROM_ME.getStringValue());
		payload.put(GcmMessageField.DATA_CREATOR_ID.getStringValue(), "test-creator-id");
		payload.put(GcmMessageField.DATA_CREATION_TIME.getStringValue(), "1000");
		payload.put(GcmMessageField.DATA_TTL.getStringValue(), "1000");

		jsonObject.put(GcmMessageField.MESSAGE_DATA.getStringValue(), payload);

		payload.put(GcmMessageField.DATA_LATITUDE.getStringValue(), Double.toString(0.0));
		payload.put(GcmMessageField.DATA_LONGITUDE.getStringValue(), Double.toString(0.0));

		payload.put(GcmMessageField.DATA_LOCALITY.getStringValue(), "test-locality");
		payload.put(GcmMessageField.DATA_COUNTRY.getStringValue(), "test-country");
		payload.put(GcmMessageField.DATA_ACQUISITION_MODE.getStringValue(), "test-acquisition-mode");
		payload.put(GcmMessageField.DATA_CREATOR_ID.getStringValue(), "test-creator-id");

		payload.put(GcmMessageField.DATA_TTL.getStringValue(), "100000000");

		payload.put(GcmMessageField.DATA_CREATION_TIME.getStringValue(), Long.toString(System.currentTimeMillis()));

		// Configure mocks
		when(resourcePersistenceService.storeResource(notNull(ResourceSharingResource.class))).thenReturn(null);
		when(userPersistenceService.readUsersByUserId(payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue())))
		.thenReturn(new ResourceSharingUser(
				payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()),
				"test-creator-gcm-id",
				new DateTime(),
				"test-creator-address",
				"test-creator-locality",
				"test-creator-country",
				0.0,
				0.0,
				0));

		List<ResourceSharingUser> usersInRange = new ArrayList<>();
		usersInRange.add(new ResourceSharingUser(
				payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()),
				"test-creator-gcm-id",
				new DateTime(),
				"test-creator-address",
				"test-creator-locality",
				"test-creator-country",
				0.0,
				0.0,
				0));
		when(userPersistenceService.findUsersInRange(
				Double.parseDouble(payload.get(GcmMessageField.DATA_LATITUDE.getStringValue())),
				Double.parseDouble(payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue())),
				new ResourceSharingUser(
						payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()),
						"test-creator-gcm-id",
						new DateTime(),
						"test-creator-address",
						"test-creator-locality",
						"test-creator-country",
						0.0,
						0.0,
						0)))
				.thenReturn(usersInRange);

		gcmMessageHandler.handleIncomingDataMessage(jsonObject);

		verify(gcmMessageSender).sendJsonAck((String) jsonObject.get(GcmMessageField.MESSAGE_FROM.getStringValue()), (String) jsonObject.get(GcmMessageField.MESSAGE_ID.getStringValue()));
		verify(resourcePersistenceService).storeResource(
				new ResourceSharingResource(
						null,
						payload.get(GcmMessageField.DATA_TITLE.getStringValue()),
						payload.get(GcmMessageField.DATA_DESCRIPTION.getStringValue()),
						Double.parseDouble(payload.get(GcmMessageField.DATA_LATITUDE.getStringValue())),
						Double.parseDouble(payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue())),
						payload.get(GcmMessageField.DATA_LOCALITY.getStringValue()),
						payload.get(GcmMessageField.DATA_COUNTRY.getStringValue()),
						new DateTime(Long.parseLong(payload.get(GcmMessageField.DATA_CREATION_TIME.getStringValue()))),
						payload.get(GcmMessageField.DATA_ACQUISITION_MODE.getStringValue()),
						payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()),
						Boolean.FALSE,
						null,
						Long.parseLong(payload.get(GcmMessageField.DATA_TTL.getStringValue()))));
		verify(userPersistenceService).findUsersInRange(
				Double.parseDouble(payload.get(GcmMessageField.DATA_LATITUDE.getStringValue())),
				Double.parseDouble(payload.get(GcmMessageField.DATA_LONGITUDE.getStringValue())),
				new ResourceSharingUser(
						payload.get(GcmMessageField.DATA_CREATOR_ID.getStringValue()),
						"test-creator-gcm-id",
						new DateTime(),
						"test-creator-address",
						"test-creator-locality",
						"test-creator-country",
						0.0,
						0.0,
						0));
	}
}
