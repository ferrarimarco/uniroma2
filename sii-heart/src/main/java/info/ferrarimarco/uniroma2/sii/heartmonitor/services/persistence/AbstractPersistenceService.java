package info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.config.SpringMongoConfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public abstract class AbstractPersistenceService {
	
	protected AbstractApplicationContext context;
	
	public AbstractPersistenceService() {
		context = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		
		open();
	}
	
	protected abstract void open();
	
	public void close(){
		context.close();
	}
	
	public abstract void deleteAll();
	
}
