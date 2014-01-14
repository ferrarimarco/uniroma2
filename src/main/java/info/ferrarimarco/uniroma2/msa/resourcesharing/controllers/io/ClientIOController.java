package info.ferrarimarco.uniroma2.msa.resourcesharing.controllers.io;

import javax.annotation.PostConstruct;

import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/client")
public class ClientIOController {
	
	private Logger logger = LoggerFactory.getLogger(ClientIOController.class);
	
	@Autowired
	private UserPersistenceService userPersistenceService;
	
	@Autowired
	private ResourcePersistenceService resourcePersistenceService;
	
	@Autowired
	private HashingService hashingService;
	
	@PostConstruct
	public void init() {
		logger.info("ClientIOController init completed");
	}

}
