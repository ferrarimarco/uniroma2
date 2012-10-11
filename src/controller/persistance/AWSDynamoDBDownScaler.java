package controller.persistance;

import java.util.TimerTask;

public class AWSDynamoDBDownScaler extends TimerTask {

	private AWSDynamoDBStorageManager awsStorageManager;
	
	public AWSDynamoDBDownScaler() {
		awsStorageManager = new AWSDynamoDBStorageManager();
	}
	
	@Override
	public void run() {
		awsStorageManager.decreaseThroughput(StorageLocation.POP3_MAILDROPS.toString());
		awsStorageManager.decreaseThroughput(StorageLocation.POP3_USERS.toString());
		awsStorageManager.decreaseThroughput(StorageLocation.TABLES_MAINTENANCE.toString());
	}
}
