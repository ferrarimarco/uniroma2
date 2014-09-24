package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm;

import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GcmController {
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private ResourcePersistenceService resourcePersistenceService;
	
	@Autowired
	private HashingService hashingService;
	
	@PostConstruct
	public void init() {
		log.trace("ClientIOController init completed");
	}
}
