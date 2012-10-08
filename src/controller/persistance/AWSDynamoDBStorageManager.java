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
import com.amazonaws.services.dynamodb.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.ReturnValue;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;
import com.amazonaws.services.dynamodb.model.UpdateItemRequest;

import controller.AbstractRequestHandler;
import controller.pop3.POP3MessageDeletion;

public class AWSDynamoDBStorageManager extends AbstractPersistantMemoryStorageManager {

	private AmazonDynamoDBClient client;

	private static final int MAX_RETRIES = 10;
	private static final int SLEEP_TIME = 500;
	
	// TODO: manage the thr exceeded condition: if the DB cannot be contacted in MAX_RETRIES tries then we have to handle this
	
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

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				getClient().putItem(putItemRequest);
				return;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {

		GetItemRequest getItemRequest = new GetItemRequest().withTableName(location.toString()).withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)))
				.withAttributesToGet(Arrays.asList(fieldName.toString()));

		GetItemResult result = null;

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				result = getClient().getItem(getItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		if (result.getItem() == null || result.getItem().get(fieldName.toString()) == null) {
			return "";
		} else {
			return result.getItem().get(fieldName.toString()).getS();
		}
	}

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

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				getClient().updateItem(updateItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void delete(StorageLocation location, String keyValue) {

		DeleteItemRequest deleteItemRequest = new DeleteItemRequest().withTableName(location.toString()).withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)));

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				getClient().deleteItem(deleteItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {

		String result = "";

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				result = read(location, fieldName, keyValue);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
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
			
			for(int j = 0; j < MAX_RETRIES; j++) {
				try {
					items.put(fieldNames.get(i).toString(), new AttributeValue(values[i]));
					break;
				} catch (ProvisionedThroughputExceededException e) {
					AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
					
					// TODO: check this nested exception
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		}

		return items;
	}

	@Override
	public List<String> scanForMessageDimensions(String clientId, String userName) {

		Condition userNameCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(userName));

		Condition messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.NO.toString()));

		Map<String, Condition> conditions = new HashMap<String, Condition>();

		conditions.put(FieldName.POP3_MESSAGE_TO.toString(), userNameCondition);
		conditions.put(FieldName.POP3_MESSAGE_TO_DELETE.toString(), messageToDeleteCondition);

		ScanRequest scanRequest = new ScanRequest().withTableName(StorageLocation.POP3_MAILDROPS.toString()).withScanFilter(conditions)
				.withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_DIMENSION.toString()));

		ScanResult result = null;

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				result = getClient().scan(scanRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		List<String> messageDimensions = new ArrayList<String>();
		
		for (Map<String, AttributeValue> item : result.getItems()) {
			messageDimensions.add(item.get(FieldName.POP3_MESSAGE_DIMENSION.toString()).getS());
		}

		return messageDimensions;
	}

	@Override
	public List<String> getMessageUIDs(StorageLocation location, String clientId, String userName, boolean isToDelete) {

		Condition userNameCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(userName));

		Condition messageToDeleteCondition = null;
		
		if(!isToDelete) {
			messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.NO.toString()));
		}else {
			messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.YES.toString()));
		}
		
		Map<String, Condition> conditions = new HashMap<String, Condition>();

		conditions.put(FieldName.POP3_MESSAGE_TO.toString(), userNameCondition);
		conditions.put(FieldName.POP3_MESSAGE_TO_DELETE.toString(), messageToDeleteCondition);

		ScanRequest scanRequest = new ScanRequest().withTableName(location.toString()).withScanFilter(conditions)
				.withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_UID.toString()));

		ScanResult result = null;

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				result = getClient().scan(scanRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		List<String> messageUIDs = new ArrayList<String>();
		
		for (Map<String, AttributeValue> item : result.getItems()) {
			messageUIDs.add(item.get(FieldName.POP3_MESSAGE_UID.toString()).getS());
		}

		return messageUIDs;
	}


	@Override
	public void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values) {
		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();

		Key key = new Key().withHashKeyElement(new AttributeValue().withS(keyValue));

		// Add two new authors to the list.
		updateItems.put(fieldName.toString(), new AttributeValueUpdate().withAction(AttributeAction.ADD).withValue(new AttributeValue().withSS(values)));

		UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(location.toString()).withKey(key).withAttributeUpdates(updateItems);

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				getClient().updateItem(updateItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue) {

		GetItemRequest getItemRequest = new GetItemRequest().withTableName(location.toString()).withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)))
				.withAttributesToGet(Arrays.asList(fieldName.toString()));

		GetItemResult result = null;

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				result = getClient().getItem(getItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		if (result.getItem() == null || result.getItem().get(fieldName.toString()) == null) {
			return new ArrayList<String>();
		} else {
			return result.getItem().get(fieldName.toString()).getSS();
		}
	}
}
