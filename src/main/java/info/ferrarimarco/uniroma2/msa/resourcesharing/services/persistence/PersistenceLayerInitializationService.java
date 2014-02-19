package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import javax.annotation.PostConstruct;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingMobileNodeData;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingResource;
import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceLayerInitializationService {
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private ResourcePersistenceService resourcePersistenceService;
	
	@Autowired
	private MobileNodeDataPersistenceService mobileNodeDataPersistenceService;
	
	@Autowired
	private HashingService hashingService;
	
	@Autowired
	private DatatypeConversionService datatypeConversionService;
	
	public static final String USER_NAME_PREFIX = "USER_";
	public static final String EMAIL_SUFFIX = "@domain.ext";
	public static final String PASSWORD = "password";
	
	@PostConstruct
	private void init() {
		userPersistenceService.open();
		resourcePersistenceService.open();
		mobileNodeDataPersistenceService.open();
	}

	public void initializeUserRepository(Integer userCount) {
		
		cleanupUserRepository();
		
		byte[] hashedPasswordBytes = hashingService.hash(PASSWORD);
    	String hashedPassword = datatypeConversionService.bytesToHexString(hashedPasswordBytes);
		
		for(int i = 0; i < userCount; i++) {
			StringBuilder userName = new StringBuilder(USER_NAME_PREFIX + i);
			userPersistenceService.storeUser(new ResourceSharingUser(userName.toString(), userName.toString() + EMAIL_SUFFIX, hashedPassword, new DateTime()));
		}
	}
	
	public void cleanupUserRepository() {
		userPersistenceService.dropCollection();
	}
	
	public void initializeResourceRepository(Integer resourceCount) {
		
		cleanupResourceRepository();
		
		for(int i = 0; i < resourceCount; i++) {
			resourcePersistenceService.storeResource(new ResourceSharingResource("Title" + i, "Description" + i, "Location" + i, "AcquisitionMode" + i, "CreatorId" + 1));
		}
	}
	
	public void cleanupResourceRepository() {
		resourcePersistenceService.dropCollection();
	}
	
	public void initializeMobileNodeDataRepository(Integer mobileNodeDataCount) {
		cleanupMobileNodeDataRepository();
		
		for(int i = 0; i < mobileNodeDataCount; i++) {
			mobileNodeDataPersistenceService.storeMobileNodeData(new ResourceSharingMobileNodeData("ID" + i, "LOCATION" + i, new DateTime()));
		}
	}
	
	public void cleanupMobileNodeDataRepository() {
		mobileNodeDataPersistenceService.dropCollection();
	}
}
