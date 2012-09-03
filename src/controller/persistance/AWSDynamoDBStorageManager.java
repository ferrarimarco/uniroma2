package controller.persistance;

import java.io.IOException;
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
import com.amazonaws.services.dynamodb.model.DeleteItemResult;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.PutItemResult;
import com.amazonaws.services.dynamodb.model.ReturnValue;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;
import com.amazonaws.services.dynamodb.model.UpdateItemRequest;
import com.amazonaws.services.dynamodb.model.UpdateItemResult;

import controller.pop3.POP3MessageDeletion;

public class AWSDynamoDBStorageManager implements PersistanceManager {

	private AmazonDynamoDBClient getClient() {
		
		AWSCredentials credentials;
		AmazonDynamoDBClient dynamoDB = null;
		try {
			credentials = new PropertiesCredentials(
					AWSDynamoDBStorageManager.class
							.getResourceAsStream("AwsCredentials.properties"));
			dynamoDB = new AmazonDynamoDBClient(credentials);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dynamoDB;
	}

	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String ...values) {
		
		Map<String, AttributeValue> item = newItem(fieldNames, values);
		
		PutItemRequest putItemRequest = new PutItemRequest(location.toString(), item);
		PutItemResult putItemResult = getClient().putItem(putItemRequest);
		
		System.out.println("Result: " + putItemResult);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {

		GetItemRequest getItemRequest = new GetItemRequest().withTableName(location.toString())
				.withKey(new Key().withHashKeyElement(new AttributeValue().withN(keyValue)))
				.withAttributesToGet(Arrays.asList(fieldName.toString()));

		GetItemResult result = getClient().getItem(getItemRequest);

		if(result.getItem().get(fieldName.toString()).getS() == null){
			return "";
		}else{
			return result.getItem().get(fieldName.toString()).getS();
		}
	}

	@Override
	public void update(StorageLocation location, FieldName fieldName, String keyValue, String newValue) {
		
		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		Key key = new Key().withHashKeyElement(new AttributeValue().withS(keyValue));

		// Update the attribute
		updateItems.put(fieldName.toString(), new AttributeValueUpdate().withAction(AttributeAction.PUT)
				.withValue(new AttributeValue(newValue)));

		UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(location.toString())
				  .withKey(key).withReturnValues(ReturnValue.ALL_NEW)
				  .withAttributeUpdates(updateItems);
		
		UpdateItemResult result = getClient().updateItem(updateItemRequest);
		
		System.out.println("Result: " + result);

	}

	@Override
	public void delete(StorageLocation location, String keyValue) {
		
		DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		.withTableName(location.toString())
		.withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)));
		
		DeleteItemResult result = getClient().deleteItem(deleteItemRequest);

		System.out.println("Result: " + result);
		
	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {
		
		String result = read(location, fieldName, keyValue);
		
		if(result.isEmpty()){
			return false;
		}else{
			return true;
		}
	}

	private Map<String, AttributeValue> newItem(List<FieldName> fieldNames, String ...values) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>(fieldNames.size());
		
		// TODO: check if filedNames and values have the same size
		
		for(int i = 0; i < fieldNames.size(); i++){
			item.put(fieldNames.get(i).toString(), new AttributeValue(values[i]));
		}
		
		return item;
	}
	
	@Override
	public void scanAndDeletePop3Messages(String keyUserName){
		
		Condition userNameCondition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ)
	    .withAttributeValueList(new AttributeValue().withS(keyUserName));
		
		Condition messageToDeleteCondition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ)
	    .withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.YES.toString()));
		
		Map<String, Condition> conditions = new HashMap<String, Condition>();
		
		conditions.put(FieldName.POP3_MESSAGE_TO.toString(), userNameCondition);
		conditions.put(FieldName.POP3_MESSAGE_TO_DELETE.toString(), messageToDeleteCondition);
		
		ScanRequest scanRequest = new ScanRequest()
		.withTableName(StorageLocation.POP3_MAILDROPS.toString())
	    .withScanFilter(conditions)
	    .withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_UID.toString()));
		
		ScanResult result = getClient().scan(scanRequest);
		
		for (Map<String, AttributeValue> item : result.getItems()) {
			delete(StorageLocation.POP3_MAILDROPS, item.get(FieldName.POP3_MESSAGE_UID).getS());
		}
	}

}
