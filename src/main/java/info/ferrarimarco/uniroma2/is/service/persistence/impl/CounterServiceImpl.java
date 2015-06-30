package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Counter;
import info.ferrarimarco.uniroma2.is.service.persistence.CounterService;

@Service
public class CounterServiceImpl implements CounterService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Dynamic init and increment are useful if more instances of this app are running in distributed environment
    // so there is no need to coordinate
    @Value("${config.persistence.counter.init}")
    private long counterInitValue;

    @Value("${config.persistence.counter.increment}")
    private long counterIncrement;

    @Override
    public long getNextCategorySequence() {
        return increaseCounter(Constants.CATEGORY_SYM_ID_COUNTER_NAME);
    }

    @Override
    public long getNextClazzSequence() {
        return increaseCounter(Constants.CLASS_SYM_ID_COUNTER_NAME);
    }

    @Override
    public long getNextProductSequence() {
        return increaseCounter(Constants.PRODUCT_SYM_ID_COUNTER_NAME);
    }
    
    @Override
    public long getNextProductEntitySequence() {
        return increaseCounter(Constants.PRODUCT_INSTANCE_COUNTER_NAME);
    }
    
    @Override
    public long getNextEntityStatSequence() {
        return increaseCounter(Constants.ENTITY_STAT_COUNTER_NAME);
    }
    
    private long increaseCounter(String counterName){
        if(!mongoTemplate.collectionExists(Counter.class)){
            mongoTemplate.createCollection(Counter.class);
        }
        
        Query query = new Query(Criteria.where("name").is(counterName));
        Update update = new Update().inc("sequence", counterIncrement);

        // return the Counter object (before the update)
        Counter counter = mongoTemplate.findAndModify(query, update, Counter.class);

        long result;

        // Create new counter if necessary
        if(counter == null){
            result = counterInitValue;

            counter = new Counter(counterName, counterInitValue + counterIncrement);
            mongoTemplate.save(counter);
        }else{
            result = counter.getSequence();
        }

        return result;
    }
}