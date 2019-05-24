package info.ferrarimarco.uniroma2.is.config.context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "info.ferrarimarco.uniroma2.is.persistence.repositories")
public class SpringMongoConfig {
    
    @Value("${config.persistence.host}")
    private String dbHost;
    
    @Value("${config.persistence.port}") 
    private String dbPort;
    
    @Value("${config.persistence.name}")
    private String dbName;
    
    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(dbHost + ":" + dbPort), dbName);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}