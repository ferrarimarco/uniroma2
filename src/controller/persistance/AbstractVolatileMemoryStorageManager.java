package controller.persistance;

import java.util.List;

public abstract class AbstractVolatileMemoryStorageManager implements PersistanceManager {

	@Override
	public void scanAndDeletePop3Messages(String clientId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> scanForMessageDimensions(String ClientId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getMessageUIDs(String ClientId) {
		// TODO Auto-generated method stub
		return null;
	}
}
