package info.ferrarimarco.uniroma2.msa.resourcesharing.services.persistence;

import info.ferrarimarco.uniroma2.msa.resourcesharing.dao.config.SpringMongoConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class AbstractMongoPersistenceService {
	
	protected AbstractApplicationContext context;
	
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	public AbstractMongoPersistenceService() {
		context = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		open();
	}
	
	protected abstract void open();
	
	public void close(){
		context.close();
	}
	
	public abstract void deleteAll();
}
