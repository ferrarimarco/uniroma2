package info.ferrarimarco.uniroma2.sii.heartmonitor.dao.config;

import info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories.HeartbeatSessionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "info.ferrarimarco.uniroma2.sii.heartmonitor.dao.repositories")
public class SpringMongoConfig {
	
	@Autowired
	private HeartbeatSessionRepository heartbeatSessionRepository;
	
	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new MongoClient(), "localhost");
	}
	
	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
 
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
 
		return mongoTemplate;
	}
}
