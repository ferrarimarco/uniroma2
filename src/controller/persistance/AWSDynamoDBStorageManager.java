package controller.persistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeAction;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.ReturnValue;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;
import com.amazonaws.services.dynamodb.model.UpdateItemRequest;

import controller.pop3.POP3MessageDeletion;

public class AWSDynamoDBStorageManager implements PersistanceManager {

	private AmazonDynamoDBClient client;

	public AWSDynamoDBStorageManager() {
		AWSCredentials credentials;
		try {
			credentials = new PropertiesCredentials(AWSDynamoDBStorageManager.class.getResourceAsStream("AwsCredentials.properties"));
			client = new AmazonDynamoDBClient(credentials);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private AmazonDynamoDBClient getClient() {
		return client;
	}

	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String... values) {

		Map<String, AttributeValue> item = newItem(fieldNames, values);

		PutItemRequest putItemRequest = new PutItemRequest(location.toString(), item);
		getClient().putItem(putItemRequest);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {

		GetItemRequest getItemRequest = new GetItemRequest().withTableName(location.toString()).withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)))
				.withAttributesToGet(Arrays.asList(fieldName.toString()));

		GetItemResult result = getClient().getItem(getItemRequest);

		if (result.getItem() == null || result.getItem().get(fieldName.toString()) == null) {
			return "";
		} else {
			return result.getItem().get(fieldName.toString()).getS();
		}
	}

	// TODO: make the update multi field to avoid multiple queries

	@Override
	public void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values) {

		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		Key key = new Key().withHashKeyElement(new AttributeValue().withS(keyValue));

		// TODO: check if filedNames and values have the same size

		for (int i = 0; i < fieldNames.size(); i++) {
			updateItems.put(fieldNames.get(i).toString(), new AttributeValueUpdate().withAction(AttributeAction.PUT).withValue(new AttributeValue(values[i])));
		}

		// Update the attributes
		UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(location.toString()).withKey(key).withReturnValues(ReturnValue.ALL_NEW).withAttributeUpdates(updateItems);

		getClient().updateItem(updateItemRequest);
	}

	@Override
	public void delete(StorageLocation location, String keyValue) {

		DeleteItemRequest deleteItemRequest = new DeleteItemRequest().withTableName(location.toString()).withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)));

		getClient().deleteItem(deleteItemRequest);
	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {

		String result = read(location, fieldName, keyValue);

		if (result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private Map<String, AttributeValue> newItem(List<FieldName> fieldNames, String... values) {

		Map<String, AttributeValue> items = new HashMap<String, AttributeValue>(fieldNames.size());

		// TODO: check if filedNames and values have the same size

		for (int i = 0; i < fieldNames.size(); i++) {
			items.put(fieldNames.get(i).toString(), new AttributeValue(values[i]));
		}

		return items;
	}

	@Override
	public void scanAndDeletePop3Messages(String clientId) {

		String userName = getClientUserName(clientId);

		// User is not authenticated
		if (userName.isEmpty()) {
			return;
		}

		Condition userNameCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(userName));

		Condition messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.YES.toString()));

		Map<String, Condition> conditions = new HashMap<String, Condition>();

		conditions.put(FieldName.POP3_MESSAGE_TO.toString(), userNameCondition);
		conditions.put(FieldName.POP3_MESSAGE_TO_DELETE.toString(), messageToDeleteCondition);

		ScanRequest scanRequest = new ScanRequest().withTableName(StorageLocation.POP3_MAILDROPS.toString()).withScanFilter(conditions)
				.withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_UID.toString()));

		ScanResult result = getClient().scan(scanRequest);

		for (Map<String, AttributeValue> item : result.getItems()) {
			delete(StorageLocation.POP3_MAILDROPS, item.get(FieldName.POP3_MESSAGE_UID.toString()).getS());
		}
	}

	@Override
	public List<String> scanForMessageDimensions(String clientId) {

		String userName = getClientUserName(clientId);

		Condition userNameCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(userName));

		Condition messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.NO.toString()));

		Map<String, Condition> conditions = new HashMap<String, Condition>();

		conditions.put(FieldName.POP3_MESSAGE_TO.toString(), userNameCondition);
		conditions.put(FieldName.POP3_MESSAGE_TO_DELETE.toString(), messageToDeleteCondition);

		ScanRequest scanRequest = new ScanRequest().withTableName(StorageLocation.POP3_MAILDROPS.toString()).withScanFilter(conditions)
				.withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_DIMENSION.toString()));

		ScanResult result = getClient().scan(scanRequest);

		List<String> messageDimensions = new ArrayList<String>();

		for (Map<String, AttributeValue> item : result.getItems()) {
			messageDimensions.add(item.get(FieldName.POP3_MESSAGE_DIMENSION.toString()).getS());
		}

		return messageDimensions;
	}

	@Override
	public List<String> getMessageUIDs(String clientId) {

		String userName = getClientUserName(clientId);

		Condition userNameCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(userName));

		Condition messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.NO.toString()));

		Map<String, Condition> conditions = new HashMap<String, Condition>();

		conditions.put(FieldName.POP3_MESSAGE_TO.toString(), userNameCondition);
		conditions.put(FieldName.POP3_MESSAGE_TO_DELETE.toString(), messageToDeleteCondition);

		ScanRequest scanRequest = new ScanRequest().withTableName(StorageLocation.POP3_MAILDROPS.toString()).withScanFilter(conditions)
				.withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_UID.toString()));

		ScanResult result = getClient().scan(scanRequest);

		List<String> messageUIDs = new ArrayList<String>();

		for (Map<String, AttributeValue> item : result.getItems()) {
			messageUIDs.add(item.get(FieldName.POP3_MESSAGE_UID.toString()).getS());
		}

		return messageUIDs;
	}

	private String getClientUserName(String clientId) {
		return read(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_USER_NAME, clientId);
	}

	@Override
	public void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values) {
		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();

		Key key = new Key().withHashKeyElement(new AttributeValue().withS(keyValue));

		// Add two new authors to the list.
		updateItems.put(fieldName.toString(), new AttributeValueUpdate().withAction(AttributeAction.ADD).withValue(new AttributeValue().withSS(values)));

		UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(location.toString()).withKey(key).withAttributeUpdates(updateItems);

		client.updateItem(updateItemRequest);
	}

	@Override
	public List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue) {

		GetItemRequest getItemRequest = new GetItemRequest().withTableName(location.toString()).withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)))
				.withAttributesToGet(Arrays.asList(fieldName.toString()));

		GetItemResult result = getClient().getItem(getItemRequest);

		if (result.getItem() == null || result.getItem().get(fieldName.toString()) == null) {
			return new ArrayList<String>();
		} else {
			return result.getItem().get(fieldName.toString()).getSS();
		}
	}
}
