package info.ferrarimarco.uniroma2.msa.resourcesharing.controllers.io;

import java.util.List;

import javax.annotation.PostConstruct;

import info.ferrarimarco.uniroma2.msa.resourcesharing.model.ResourceSharingUser;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.ResourcePersistenceService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence.UserPersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
	
	@RequestMapping(value="/user/{userName:.+}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ResourceSharingUser getUserDetails(@PathVariable("userName") String userName) {
		List<ResourceSharingUser> users = userPersistenceService.readUsersByEmail(userName);
		
		ResourceSharingUser user = null;
		
		if(users.size() == 1) {
			user = users.get(0);
		}
		
		return user;
	}
}
