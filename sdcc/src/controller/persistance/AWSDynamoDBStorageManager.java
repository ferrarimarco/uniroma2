package controller.persistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import com.amazonaws.services.dynamodb.model.DescribeTableRequest;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodb.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.QueryRequest;
import com.amazonaws.services.dynamodb.model.QueryResult;
import com.amazonaws.services.dynamodb.model.ReturnValue;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;
import com.amazonaws.services.dynamodb.model.TableDescription;
import com.amazonaws.services.dynamodb.model.UpdateItemRequest;
import com.amazonaws.services.dynamodb.model.UpdateTableRequest;

import controller.AbstractRequestHandler;
import controller.pop3.POP3MessageDeletion;

public class AWSDynamoDBStorageManager extends AbstractPersistantMemoryStorageManager {

	private AmazonDynamoDBClient client;

	private static final int MAX_RETRIES = 10;
	private static final int SLEEP_TIME = 5;
	private static final long MAX_READ_THR = 100L;
	private static final long MAX_WRITE_THR = 100L;
	public static final long MIN_READ_THR = 5L;
	public static final long MIN_WRITE_THR = 5L;
	public static final long READ_THR_INCREMENT = 6L;
	public static final long WRITE_THR_INCREMENT = 6L;
	public static final long READ_THR_DECREMENT = 3L;
	public static final long WRITE_THR_DECREMENT = 3L;
	private static final long COOLDOWN = 1200000L; // 20 minutes 
	
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

	private void increaseThroughput(String tableName) {
		TableDescription tableDescription = getClient().describeTable(new DescribeTableRequest().withTableName(tableName)).getTable();

		long readCapacity = tableDescription.getProvisionedThroughput().getReadCapacityUnits();
		long writeCapacity = tableDescription.getProvisionedThroughput().getWriteCapacityUnits();

		if((writeCapacity <= MAX_WRITE_THR - WRITE_THR_INCREMENT) && (readCapacity <= MAX_READ_THR - READ_THR_INCREMENT)) {

			ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
			.withReadCapacityUnits(readCapacity + READ_THR_INCREMENT)
			.withWriteCapacityUnits(writeCapacity + WRITE_THR_INCREMENT);

			AbstractRequestHandler.log.info("Increasing capacity of " + tableName + " to (r,w) " + (readCapacity + READ_THR_INCREMENT) + " " + (writeCapacity + WRITE_THR_INCREMENT));

			UpdateTableRequest updateTableRequest = new UpdateTableRequest()
			.withTableName(tableName)
			.withProvisionedThroughput(provisionedThroughput);

			getClient().updateTable(updateTableRequest);
			
			long timeMillis = Calendar.getInstance().getTimeInMillis();
			
			String timeNow = Long.toString(timeMillis);
			
			update(StorageLocation.TABLES_MAINTENANCE, tableName, FieldName.getMaintenanceFields() , timeNow);
			
		}else {
			AbstractRequestHandler.log.info("Cannot increase throughput anymore for table: " + tableName);
		}
	}

	public void decreaseThroughput(String tableName) {
		
		TableDescription tableDescription = getClient().describeTable(new DescribeTableRequest().withTableName(tableName)).getTable();

		long readCapacity = tableDescription.getProvisionedThroughput().getReadCapacityUnits();
		long writeCapacity = tableDescription.getProvisionedThroughput().getWriteCapacityUnits();

		// Read update time
		long updateTime = Long.parseLong(read(StorageLocation.TABLES_MAINTENANCE, FieldName.UPDATE_TIME, tableName));
		long timeMillis = Calendar.getInstance().getTimeInMillis();
		
		if (timeMillis - updateTime > COOLDOWN) {
			if ((writeCapacity >= MIN_WRITE_THR + WRITE_THR_DECREMENT) && (readCapacity >= MIN_READ_THR + READ_THR_DECREMENT)) {

				ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput().withReadCapacityUnits(readCapacity - READ_THR_DECREMENT).withWriteCapacityUnits(writeCapacity - WRITE_THR_DECREMENT);

				AbstractRequestHandler.log.info("Decreasing capacity of " + tableName + " to (r,w) " + (readCapacity - READ_THR_DECREMENT) + " " + (writeCapacity - WRITE_THR_DECREMENT));

				UpdateTableRequest updateTableRequest = new UpdateTableRequest().withTableName(tableName).withProvisionedThroughput(provisionedThroughput);

				getClient().updateTable(updateTableRequest);

				timeMillis = Calendar.getInstance().getTimeInMillis();

				String timeNow = Long.toString(timeMillis);

				update(StorageLocation.TABLES_MAINTENANCE, tableName, FieldName.getMaintenanceFields(), timeNow);
			} else {
				AbstractRequestHandler.log.info("Cannot decrease throughput anymore for table: " + tableName);
			}
		}
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
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
					
					// Try to increase provisioned thr and retry
					if(i == MAX_RETRIES - 1) {
						// Restart
						i = -1;
						
						increaseThroughput(location.toString());
					}
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			

		}
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {
		
		GetItemRequest getItemRequest = new GetItemRequest().withTableName(location.toString()).withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)))
				.withAttributesToGet(Arrays.asList(fieldName.toString())).withConsistentRead(true);

		GetItemResult result = null;
		
		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				result = getClient().getItem(getItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
					
					// Try to increase provisioned thr and retry
					if(i == MAX_RETRIES - 1) {
						// Restart
						i = -1;
						
						increaseThroughput(location.toString());
					}
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		if (result.getItem() == null || result.getItem().get(fieldName.toString()) == null) {
			return "";
		} else {
			if(fieldName.equals(FieldName.USER_MESSAGES_NUMBER) || fieldName.equals(FieldName.MESSAGES_TOTAL_DIMENSION)) {
				return result.getItem().get(fieldName.toString()).getN();
			}else {
				return result.getItem().get(fieldName.toString()).getS();
			}
		}
	}

	@Override
	public void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values) {

		UpdateItemRequest updateItemRequest = null;
		
		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		Key key = new Key().withHashKeyElement(new AttributeValue().withS(keyValue));
		
		if(!location.equals(StorageLocation.POP3_USERS)) {
			// TODO: check if filedNames and values have the same size

			for (int i = 0; i < fieldNames.size(); i++) {
				updateItems.put(fieldNames.get(i).toString(), new AttributeValueUpdate().withAction(AttributeAction.PUT).withValue(new AttributeValue(values[i])));
			}
		}else {
			for (int i = 0; i < fieldNames.size(); i++) {
				updateItems.put(fieldNames.get(i).toString(), new AttributeValueUpdate().withAction(AttributeAction.ADD).withValue(new AttributeValue().withN(values[i])));
			}
		}
		
			// Update the attributes
			updateItemRequest = new UpdateItemRequest()
			.withTableName(location.toString())
			.withKey(key)
			.withReturnValues(ReturnValue.ALL_NEW) //TODO: is this necessary?
			.withAttributeUpdates(updateItems);
		
		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				getClient().updateItem(updateItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
					
					// Try to increase provisioned thr and retry
					if(i == MAX_RETRIES - 1) {
						// Restart
						i = -1;
						
						increaseThroughput(location.toString());
					}					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void delete(StorageLocation location, String keyValue) {

		DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
			.withTableName(location.toString())
			.withKey(new Key().withHashKeyElement(new AttributeValue().withS(keyValue)));

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				getClient().deleteItem(deleteItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
					
					// Try to increase provisioned thr and retry
					if(i == MAX_RETRIES - 1) {
						// Restart
						i = -1;
						
						increaseThroughput(location.toString());
					}
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void delete(StorageLocation location, String keyValue, String rangeKey) {
		
		DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
			.withTableName(location.toString())
			.withKey(new Key()
				.withHashKeyElement(new AttributeValue().withS(keyValue))
				.withRangeKeyElement(new AttributeValue().withS(rangeKey)));

		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				getClient().deleteItem(deleteItemRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
					
					// Try to increase provisioned thr and retry
					if(i == MAX_RETRIES - 1) {
						// Restart
						i = -1;
						
						increaseThroughput(location.toString());
					}
					
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
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
					
					// Try to increase provisioned thr and retry
					if(i == MAX_RETRIES - 1) {
						// Restart
						i = -1;
						
						increaseThroughput(location.toString());
					}
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
		}

		return items;
	}

	@Override
	public List<String> scanForMessageDimensions(StorageLocation location, String clientId, String userName, boolean isToDelete) {

		Condition userNameCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(userName));

		Condition messageToDeleteCondition = null;
		
		if(!isToDelete){
			messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.NO.toString()));
		}else{
			messageToDeleteCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(POP3MessageDeletion.YES.toString()));
		}
		
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
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
					
					// Try to increase provisioned thr and retry
					if(i == MAX_RETRIES - 1) {
						// Restart
						i = -1;
						
						increaseThroughput(location.toString());
					}
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
		/*
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
				.withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_UID.toString(), FieldName.POP3_MESSAGE_DIMENSION.toString()));

		ScanResult result = null;
		*/
		
		QueryRequest queryRequest = new QueryRequest()
		.withTableName(location.toString())
		.withHashKeyValue(new AttributeValue().withS(userName))
		.withAttributesToGet(Arrays.asList(FieldName.POP3_MESSAGE_UID.toString(), FieldName.POP3_MESSAGE_DIMENSION.toString()))
		.withScanIndexForward(true)
		.withConsistentRead(true);

		QueryResult result = null;
		
		for(int i = 0; i < MAX_RETRIES; i++) {
			try {
				//result = getClient().scan(scanRequest);
				result = getClient().query(queryRequest);
				break;
			} catch (ProvisionedThroughputExceededException e) {
				AbstractRequestHandler.log.info("----------------- THR EXCEDEED ("+ Thread.currentThread().getId() + "): Tentativo Numero " + i + " fallito. Riprovo");
				
				// TODO: check this nested exception
				try {
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		List<String> messageUIDs = new ArrayList<String>();
		
		for (Map<String, AttributeValue> item : result.getItems()) {
			messageUIDs.add(item.get(FieldName.POP3_MESSAGE_UID.toString()).getS());
			messageUIDs.add(item.get(FieldName.POP3_MESSAGE_DIMENSION.toString()).getS());
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
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
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
					// Exponential backoff
					double endInterval = (int) Math.pow(2, i);
					int delay = SLEEP_TIME + (int) (SLEEP_TIME * Math.random() * (endInterval + 1));
					AbstractRequestHandler.log.info("Aspetto per " + delay + " ms");
					Thread.sleep(delay);
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
