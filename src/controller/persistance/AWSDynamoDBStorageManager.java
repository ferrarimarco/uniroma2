package controller.persistance;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.PutItemResult;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(StorageLocation location, FieldName fieldName, String keyValue) {
		// TODO Auto-generated method stub

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
	
	

}
